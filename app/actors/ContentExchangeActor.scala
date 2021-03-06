/*
 * Copyright 2014-2016 Fabio Tiriticco, Fabway
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

import java.util.concurrent.TimeoutException
import javax.inject.Inject

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.pattern.ask
import consts.json.{JsonInputLabels, JsonResponseLabels}
import consts.{Areas, MatchCriteria, Timeouts}
import helpers.json.{JsonInputHelper, JsonMessageHelper, JsonResponseHelper}
import helpers.movements.SwipeMovementHelper
import helpers.requests.RequestValidityHelper
import models.matching.base.MatchRequestFactory
import models.matching.{Delivery, GroupComMatchRequest, GroupCreateMatchRequest, Matchee}
import models.messages.actors._
import models.messages.client._
import models.messages.client.base.ClientInputMatchRequest
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise

import scala.util.{Failure, Success, Try}

/** Will manage a client connection and take the appropriate action upon receiving a client input.
  *
  */
class ContentExchangeActor @Inject()(client: ConnectedClient,
                                     swipeMatcherActor: ActorRef,
                                     pinchMatcherActor: ActorRef,
                                     universalMatcherActor: ActorRef) extends Actor {

  lazy val getUserLog = s"[dev ${client.deviceId}] "

  def logInfo(message: String) = Logger.info(getUserLog + message)

  def logDebug(message: String) = Logger.debug(getUserLog + message)

  def logError(message: String) = Logger.error(getUserLog + message)

  logInfo(s"client connected, ip: ${client.remoteAddress}, managed by $self")

  // initiate a timeout which will close the connection after the timeout
  val timeoutKiller = scala.concurrent.Promise[Int]()
  Promise.timeout({
    if (!timeoutKiller.isCompleted) {
      logInfo(s"timeout expired. Closing connection with client at ${client.remoteAddress}.")
      closeClientConnection()
    }
  }, Timeouts.maxConnectionLifetime)

  var matchees: Option[List[Matchee]] = None
  var myself: Option[Matchee] = None
  var groupId: Option[String] = None

  // I don't like this trick, not one bit. But I can't rely on the values of
  //  groupId or myInfo, as if a connection is established and immediately broken
  //  before the matching timeout expires, then we would still send the timeout message
  //  to the client. Maybe I can find a better way.
  var needsToBeMatched: Option[String] = None

  // *************************************
  // Actor messaging
  // *************************************

  /** Deals with messages sent to this actor.
    *
    */
  def receive: Actor.Receive = {

    // data received from the client that this actor is managing
    case input: String => self ! Input(input)
    case Input(input) => onInput(input)

    // messages received from other actors
    case Matched(criteria, movementType, groupMatchees, groupUniqueId, scheme) =>
      // the assumption is that the info we got is valid
      val (me, others) = groupMatchees.partition(m => m.handlingActor == self)
      myself = Some(me.head)
      matchees = Some(others)
      groupId = Some(groupUniqueId)
      needsToBeMatched = None

      logInfo(s"matched. Group id: $groupUniqueId, groupMatchees: $matchees")
      val jsonToSend = JsonResponseHelper.createMatchedResponse(criteria, movementType, me.head, groupMatchees, groupUniqueId, scheme)
      sendToClient(jsonToSend)

    case MatchedInGroup(criteria, movementType, groupMatchees) =>
      // TODO: more checks
      needsToBeMatched = None

      if (groupId.isDefined) {
        val jsonToSend = JsonResponseHelper.createMatchedInGroupResponse(criteria, movementType, groupId.get, groupMatchees)
        sendToClient(jsonToSend)
      } else {
        // TODO
      }

    case MatcheeLeftGroup(matchee, reason) =>
      if (groupId.isDefined) {
        logInfo(s"matchee left group: ${groupId.get}, matchee: $matchee, reason: $reason")

        // notify client of broken group
        val message = JsonMessageHelper.createMatcheeLeftGroupMessage(groupId.get, matchee.idInGroup)
        sendToClient(message)

        // update information about my group
        // TODO: we leave the group standing, for now
        val newMatchees = matchees.get.filterNot(_.idInGroup == matchee.idInGroup)
        if (newMatchees.isEmpty) matchees = None else matchees = Some(newMatchees)

      } else {
        logInfo(s"matchee left group, but my groupId is empty. Matchee: $matchee, reason: $reason")
      }

    case MatcheeDelivers(matchee, delivery) =>
      if (groupId.isDefined) {
        logInfo(s"matchee delivered payload. Matchee: $matchee, payload length: ${delivery.payload.length}")
        val payloadMsg = JsonMessageHelper.createMatcheeSendsPayloadMessage(groupId.get, matchee.idInGroup, delivery)
        sendToClient(payloadMsg)
      } else {
        logInfo(s"matchee delivered payload. Matchee: $matchee, but I'm not part of any group!")
      }
  }

  // *************************************
  // ContentExchangeActor lifecycle
  // *************************************
  override def postStop() = {
    if (!timeoutKiller.isCompleted) {
      // the client disconnected
      logInfo(s"client disconnected: ${client.remoteAddress}")
      if (matchees.isDefined && myself.isDefined) {
        sendMessageToMatchees(MatcheeLeftGroup(myself.get, None))
      }
    }
    timeoutKiller success 0
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
    logDebug(s"client input. Length: ${input.length}")

    // try to parse it to Json
    Try(JsonInputHelper.parseInput(input)) match {

      case Success(parsedMessage) =>

        // successfully parsed
        parsedMessage match {
          case disconnect: ClientInputMsgDisconnect => onDisconnectInput(disconnect)
          case group: ClientInputMatchRequest => onMatchInput(group)
          case leaveGroup: ClientInputMsgLeaveGroup => onLeaveGroupInput(leaveGroup)
          case delivery: ClientInputMsgDelivery => onDeliveryInput(delivery)
        }

      case Failure(e) =>
        // TODO: more descriptive error
        lazy val invalidJson = "Error parsing the JSON input."
        logInfo(s"couldn't parse input: $e")
        sendToClient(JsonResponseHelper.getInvalidInputResponse(Some(invalidJson)))
    }
  }

  /** Handles match requests.
    *
    * It will try to establish a group with previously received requests. Will inform the connected client and
    * the actors managing the other clients in the group that has been formed.
    *
    * @param matchInput the match request
    */
  def onMatchInput(matchInput: ClientInputMatchRequest) = {

    val testValidity = Try(RequestValidityHelper.matchRequestIsValid(matchInput, groupId.getOrElse("")))

    testValidity match {

      case Failure(e) =>
        // request issue
        logInfo(s"match request invalid, exception: $e")
        sendToClient(JsonResponseHelper.getInvalidMatchRequestResponse(e.getMessage))

      case Success(isValid) =>
        logInfo(s"match request valid.")

        // add request to the matcher queue
        val timestamp = System.currentTimeMillis
        val areaStart = Areas.getAreaFromString(matchInput.areaStart)
        val areaEnd = Areas.getAreaFromString(matchInput.areaEnd)
        val movement = SwipeMovementHelper.swipesToMovement(areaStart, areaEnd)
        val matchRequest = MatchRequestFactory.getMatchRequest(client.deviceId, timestamp, areaStart, areaEnd, movement, self, matchInput)

        def futureMatched(matcher: ActorRef) = {
          val matchedFuture = matchRequest match {
            case comR: GroupComMatchRequest =>
              needsToBeMatched = Some(JsonInputLabels.INPUT_TYPE_MATCH_IN_GROUP)
              (matcher ? NewComRequest(comR)) (Timeouts.maxOldestRequestInterval)
            case creR: GroupCreateMatchRequest =>
              needsToBeMatched = Some(JsonInputLabels.INPUT_TYPE_MATCH_CREATE)
              (matcher ? NewCreateRequest(creR)) (Timeouts.maxOldestRequestInterval)
          }
          matchedFuture recover {
            // basically this timeout will always trigger, but maybe we've been matched in the meantime.
            case t: TimeoutException =>
              needsToBeMatched.foreach {
                case JsonInputLabels.INPUT_TYPE_MATCH_IN_GROUP => sendToClient(JsonResponseHelper.getGroupComTimeoutResponse)
                case JsonInputLabels.INPUT_TYPE_MATCH_CREATE => sendToClient(JsonResponseHelper.getGroupCreateTimeoutResponse)
              }
              needsToBeMatched = None
          }
        }

        val criteria = MatchCriteria.getMatchCriteriaFromString(matchInput.criteria)
        criteria match {
          // we only get here if the criteria is valid
          case MatchCriteria.SWIPE => futureMatched(swipeMatcherActor)
          case MatchCriteria.PINCH => futureMatched(pinchMatcherActor)
          case MatchCriteria.UNIVERSAL => futureMatched(universalMatcherActor)
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
  def onDisconnectInput(disconnect: ClientInputMsgDisconnect) = {
    // a disconnect message doesn't have a groupId
    closeClientConnection()
  }

  /** Handles requests to leave a group.
    *
    * Will check if the client is effectively part of a group and, in case, inform the other memebers that this
    * client has left the group.
    *
    * @param leaveGroupMessage the request to leave the group
    */
  def onLeaveGroupInput(leaveGroupMessage: ClientInputMsgLeaveGroup) = {
    def getLeaveGroupLog(msg: String) = s"leave group input, $msg, group id: ${leaveGroupMessage.groupId}, reason: ${leaveGroupMessage.reason}"

    // if not valid, this input will be simply discarded
    if (groupsAreValid(leaveGroupMessage.groupId)) {
      // send messages to the other ones in the connection and simply forget about them
      matchees match {
        case Some(matcheesList) =>
          logInfo(getLeaveGroupLog("notifying other members"))

          if (myself.isDefined) {
            sendMessageToMatchees(MatcheeLeftGroup(myself.get, leaveGroupMessage.reason))
            sendToClient(JsonResponseHelper.getGroupLeftResponse(groupId.get))
          } else {
            logError(getLeaveGroupLog("myself is empty"))
          }

          leaveGroup()
        case None =>
          logError(getLeaveGroupLog("matchees is empty"))
          sendToClient(JsonResponseHelper.getNotPartOfGroupResponse(leaveGroupMessage.groupId))
      }
    } else {
      logInfo(getLeaveGroupLog("client is not part of group"))
      sendToClient(JsonResponseHelper.getNotPartOfGroupResponse(leaveGroupMessage.groupId))
    }
  }

  /** Handles requests to deliver content to other clients.
    *
    * @param clientDelivery the delivery request containing some payload
    */
  def onDeliveryInput(clientDelivery: ClientInputMsgDelivery) = {
    def getDeliveryLog(msg: String = "") = s"client delivery," +
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
      if (listRecipients.nonEmpty) {
        // create a delivery message and deliver it to the right recipients!
        val delivery: Delivery = new Delivery(clientDelivery.deliveryId, clientDelivery.payload, clientDelivery.tag,
          clientDelivery.chunkNr, clientDelivery.totalChunks)
        val message = MatcheeDelivers(myself.get, delivery)

        listRecipients.foreach(_ ! message)
      }
    } else {
      logInfo(getDeliveryLog(s"client is not part of this group"))
      sendToClient(JsonResponseHelper.getPayloadNotDeliveredResponse(clientDelivery.groupId,
        Some(JsonResponseLabels.REASON_NOT_PART_OF_THIS_GROUP)))
    }
  }

  // *************************************
  // General functions section
  // *************************************
  def groupsAreValid(inputGroupId: String) = groupId.isDefined && inputGroupId == groupId.get

  def sendToClient(message: String) = client.outActor ! message

  def closeClientConnection() = self ! PoisonPill

  def leaveGroup() = {
    matchees = None
    myself = None
    groupId = None
  }

  def sendMessageToMatchees(message: MatcheeMessage) = {
    matchees match {
      case Some(matcheeList) => matcheeList.foreach(matchee => matchee.handlingActor ! message)
      case None => // do nothing
    }
  }
}

object ContentExchangeActor {
  def props(client: ConnectedClient,
            swipeMatcherActor: ActorRef,
            pinchMatcherActor: ActorRef,
            universalMatcherActor: ActorRef) = Props(new ContentExchangeActor(client, swipeMatcherActor, pinchMatcherActor, universalMatcherActor))
}
