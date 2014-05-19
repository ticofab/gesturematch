/*
 * Copyright 2014 Fabio Tiriticco, Fabway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package actors

import akka.actor.{PoisonPill, ActorRef, Actor, Props}
import akka.pattern.ask
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models._
import scala.util.Try
import models.ClientInputMessages._
import consts.{Timeouts, Areas, Criteria}
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.concurrent.Promise
import helpers.json.{JsonMessageHelper, JsonResponseHelper, JsonInputHelper}
import helpers.movements.SwipeMovementHelper
import controllers.ApplicationWS
import consts.json.JsonResponseLabels
import java.util.concurrent.TimeoutException
import models.NewRequest
import models.ConnectedClient
import scala.util.Failure
import scala.Some
import models.Matched
import models.MatcheeLeftGroup
import scala.util.Success
import models.MatcheeDelivers
import helpers.requests.RequestValidityHelper
import helpers.storage.DBHelper

/** Will manage a client connection and take the appropriate action upon receiving a client input.
  *
  */
class ContentExchangeActor extends Actor {
  var client: Option[ConnectedClient] = None
  var channel: Option[Concurrent.Channel[String]] = None
  var matchees: Option[List[Matchee]] = None
  var myself: Option[Matchee] = None
  var groupId: Option[String] = None

  // I don't like this trick, not one bit. But I can't rely on the values of
  //  groupId or myInfo, as if a connection is established and immediately broken
  //  before the matching timeout expires, then we would still send the timeout message
  //  to the client. Maybe I can find a better way.
  var hasBeenMatched: Boolean = false

  // *************************************
  // Actor messaging
  // *************************************

  /** Deals with messages sent to this actor.
    *
    */
  def receive: Actor.Receive = {
    case connectedClient@ConnectedClient(remoteAddress, apiKey, appId, os, deviceId) =>

      def shutdownMyself() = {
        Logger.info(s"$self, shutdown")
        channel = None
        self ! PoisonPill
      }

      Logger.info(s"$self, client connected: $remoteAddress, $apiKey, $appId, $os, $deviceId")

      client = Some(connectedClient)

      val in: Iteratee[String, Unit] = Iteratee.foreach[String] {
        // sending a message to self, so they are nicely pipelined
        input => self ! Input(input)
      }.map(_ => {
        // the client disconnected
        Logger.info(s"$self, client disconnected: $remoteAddress, $deviceId")
        if (matchees.isDefined && myself.isDefined) {
          sendMessageToMatchees(MatcheeLeftGroup(myself.get, None))
        }
        shutdownMyself()
      })

      val out: Enumerator[String] = Concurrent.unicast(c => {
        // onStart
        channel = Some(c)

        // start a timeout to close the connection after a while
        Promise.timeout({
          Logger.info(s"$self, timeout expired. Closing connection with client at ${client.foreach(_.remoteAddress)}.")
          closeClientConnection()
        }, Timeouts.maxConnectionLifetime)
      }, {
        // onComplete
        Logger.debug("onComplete")
      }, (s, i) => {
        Logger.error(s"onError: $s, input: $i")
        shutdownMyself()
      })

      val wsLink = (in, out)
      sender ! wsLink

    case Input(input) => onInput(input)

    case Matched(groupMatchees, groupUniqueId, scheme) =>
      // the assumption is that the info we got is valid
      val (me, others) = groupMatchees.partition(m => m.handlingActor == self)
      myself = Some(me.head)
      matchees = Some(others)
      groupId = Some(groupUniqueId)
      hasBeenMatched = true
      Logger.info(s"$self, matched. Group id: $groupUniqueId, groupMatchees: $matchees")
      val jsonToSend = JsonResponseHelper.createMatchedResponse(me.head, groupMatchees, groupUniqueId, scheme)
      sendToClient(jsonToSend)

    case MatcheeLeftGroup(matchee, reason) =>
      if (groupId.isDefined) {
        Logger.info(s"$self, matchee left group: ${groupId.get}, matchee: $matchee, reason: $reason")
        val message = JsonMessageHelper.createMatcheeLeftGroupMessage(groupId.get, matchee.idInGroup)
        sendToClient(message)
      } else {
        Logger.error(s"$self, matchee left group, but my groupId is empty. Matchee: $matchee, reason: $reason")
      }

    case MatcheeDelivers(matchee, delivery) =>
      if (groupId.isDefined) {
        Logger.info(s"$self, matchee delivered payload. Matchee: $matchee, payload length: ${delivery.payload.length}")
        val payloadMsg = JsonMessageHelper.createMatcheeSendsPayloadMessage(groupId.get, matchee.idInGroup, delivery)
        sendToClient(payloadMsg)
      } else {
        Logger.error(s"$self, matchee delivered payload. Matchee: $matchee, but I'm not part of any group!")
      }
  }

  // *************************************
  // Events section
  // *************************************

  /** Deals with a raw input coming from the connected client.
    * It parses it and then triggers the necessary action.
    *
    * @param input the input coming from the client through the WebSocket channel.
    */
  def onInput(input: String) = {
    Logger.info(s"$self, client input. Length: ${input.length}")

    // try to parse it to Json
    Try(JsonInputHelper.parseInput(input)) match {

      case Success(parsedMessage) =>

        // successfully parsed
        parsedMessage match {
          case disconnect: ClientInputMessageDisconnect => onDisconnectInput(disconnect)
          case matchRequest: ClientInputMessageMatch => onMatchInput(matchRequest)
          case leaveGroup: ClientInputMessageLeaveGroup => onLeaveGroupInput(leaveGroup)
          case delivery: ClientInputMessageDelivery => onDeliveryInput(delivery)
        }

      case Failure(e) =>
        // TODO: more descriptive error
        lazy val invalidJson = "Error parsing the JSON input."
        Logger.info(s"$self, couldn't parse input: $e}")
        sendToClient(JsonResponseHelper.getInvalidInputResponse(Some(invalidJson)))
    }
  }

  /** Handles match requests.
    *
    * It will try to establish a group with previously received requests. Will inform the connected client and
    * the actors managing the other clients in the group that has been formed.
    *
    * @param matchRequest the match request
    */
  def onMatchInput(matchRequest: ClientInputMessageMatch) = {
    val areaStart = Areas.getAreaFromString(matchRequest.areaStart)
    val areaEnd = Areas.getAreaFromString(matchRequest.areaEnd)
    val criteria = Criteria.getCriteriaFromString(matchRequest.criteria)
    val testValidity = Try(RequestValidityHelper.matchRequestIsValid(criteria, areaStart, areaEnd))

    testValidity match {

      case Failure(e) =>
        // request issue
        Logger.info(s"$self, match request invalid, exception: $e")
        sendToClient(JsonResponseHelper.getInvalidMatchRequestResponse(e.getMessage))

      case Success(isValid) =>
        Logger.info(s"$self, match request valid.")

        if (client.isDefined) {
          val apiKey = client.get.apiKey
          val appId = client.get.appId

          DBHelper.addMatchRequest(apiKey, appId)

          // add request to the matcher queue
          val timestamp = System.currentTimeMillis
          val movement = SwipeMovementHelper.swipesToMovement(areaStart, areaEnd)
          val requestData = new RequestToMatch(apiKey, appId, client.get.deviceId,
            matchRequest.latitude, matchRequest.longitude, timestamp, areaStart, areaEnd, movement,
            matchRequest.equalityParam, self)

          def futureMatched(matcher: ActorRef) = {
            val matchedFuture = (matcher ? NewRequest(requestData))(Timeouts.maxOldestRequestInterval)
            matchedFuture recover {
              // basically this timeout will always trigger, but maybe we've been matched in the meantime.
              case t: TimeoutException => if (!hasBeenMatched) sendToClient(JsonResponseHelper.getTimeoutResponse)
            }
          }

          criteria match {
            // we only get here if the criteria is valid
            case Criteria.SWIPE => futureMatched(ApplicationWS.swipeMatchingActor)
            case Criteria.PINCH => futureMatched(ApplicationWS.pinchMatchingActor)
          }
        }

    }
  }

  /** Handles disconnect requests.
    *
    * Will send acknowledge of the disconnect request. If the disconnecting client is part of a group, the other
    * members will be informed. Finally, the connection will be closed.
    *
    * @param disconnect the disconnect request
    */
  def onDisconnectInput(disconnect: ClientInputMessageDisconnect) = {
    // a disconnect message doesn't have a groupId
    Logger.info(s"$self, disconnect input, reason: ${disconnect.reason}")
    if (matchees.isDefined && myself.isDefined) {
      sendMessageToMatchees(MatcheeLeftGroup(myself.get, disconnect.reason))
      leaveGroup()
    }
    closeClientConnection()
  }

  /** Handles requests to leave a group.
    *
    * Will check if the client is effectively part of a group and, in case, inform the other memebers that this
    * client has left the group.
    *
    * @param leaveGroupMessage the request to leave the group
    */
  def onLeaveGroupInput(leaveGroupMessage: ClientInputMessageLeaveGroup) = {
    def getLeaveGroupLog(msg: String) = {
      s"$self, leave group input, $msg, group id: ${leaveGroupMessage.groupId}, reason: ${leaveGroupMessage.reason}"
    }

    // if not valid, this input will be simply discarded
    if (groupsAreValid(leaveGroupMessage.groupId)) {
      // send messages to the other ones in the connection and simply forget about them
      matchees match {
        case Some(matcheesList) =>
          Logger.info(getLeaveGroupLog("notifying other members"))

          if (myself.isDefined) {
            sendMessageToMatchees(MatcheeLeftGroup(myself.get, leaveGroupMessage.reason))
            sendToClient(JsonResponseHelper.getGroupLeftResponse(groupId.get))
          } else {
            Logger.error(getLeaveGroupLog("myself is empty"))
          }

          leaveGroup()
        case None =>
          Logger.error(getLeaveGroupLog("matchees is empty"))
          sendToClient(JsonResponseHelper.getNotPartOfGroupResponse(leaveGroupMessage.groupId))
      }
    } else {
      Logger.info(getLeaveGroupLog("client is not part of group"))
      sendToClient(JsonResponseHelper.getNotPartOfGroupResponse(leaveGroupMessage.groupId))
    }
  }

  /** Handles requests to deliver content to other clients.
    *
    * @param clientDelivery the delivery request containing some payload
    */
  def onDeliveryInput(clientDelivery: ClientInputMessageDelivery) = {
    def getDeliveryLog(msg: String = "") = s"$self, client delivery," +
      s" $msg, groupId: ${clientDelivery.groupId}, recipients: ${clientDelivery.recipients} " +
      s", payload length: ${clientDelivery.payload.length}"

    // if not valid, this input will be simply discarded
    if (groupsAreValid(clientDelivery.groupId) && myself.isDefined && matchees.isDefined) {

      // creates a list of recipients. If none is specified, take all.
      val listRecipients: List[ActorRef] =
        clientDelivery.recipients match {
          case None | Some(Nil) => matchees.get.map(_.handlingActor)

          case Some(recipients) => for {
            matchee <- matchees.get
            recipient <- recipients
            if matchee.idInGroup == recipient && recipient != -1
          } yield matchee.handlingActor
        }

      // deliver stuff
      if (!listRecipients.isEmpty) {
        // create a delivery message and deliver it to the right recipients!
        val delivery: Delivery = new Delivery(clientDelivery.deliveryId, clientDelivery.payload,
          clientDelivery.chunkNr, clientDelivery.totalChunks)
        val message = MatcheeDelivers(myself.get, delivery)

        listRecipients.foreach(_ ! message)

        if (client.isDefined) {
          val apiKey = client.get.apiKey
          val appId = client.get.appId
          val payloadLength = clientDelivery.payload.length
          DBHelper.addPayloadReceived(apiKey, appId, payloadLength)
          DBHelper.addPayloadDelivered(apiKey, appId, payloadLength * listRecipients.length)
        }
      }
    } else {
      Logger.info(getDeliveryLog(s"client is not part of this group"))
      sendToClient(JsonResponseHelper.getPayloadNotDeliveredResponse(clientDelivery.groupId,
        Some(JsonResponseLabels.REASON_NOT_PART_OF_THIS_GROUP)))
    }
  }

  // *************************************
  // General functions section
  // *************************************
  def groupsAreValid(inputGroupId: String) = groupId.isDefined && inputGroupId == groupId.get

  def sendToClient(message: String) = channel.foreach(x => x.push(message))

  def closeClientConnection() = channel.foreach(x => x.eofAndEnd())

  def leaveGroup() = {
    matchees = None
    myself = None
    groupId = None
    hasBeenMatched = false
  }

  def sendMessageToMatchees(message: MatcheeMessage) = {
    matchees match {
      case Some(matcheeList) => matcheeList.foreach(matchee => matchee.handlingActor ! message)
      case None => // do nothing
    }
  }
}

object ContentExchangeActor {
  def props: Props = Props(classOf[ContentExchangeActor])
}
