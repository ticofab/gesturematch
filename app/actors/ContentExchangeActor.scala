package actors

import akka.actor.{ActorRef, Actor, Props}
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
  def receive: Actor.Receive = {
    case ConnectedClient(remoteAddress, apiKey, appId, os, deviceId) =>
      Logger.info(s"$self, client connected: $remoteAddress, $apiKey, $appId, $os, $deviceId")
      client = Some(ConnectedClient(remoteAddress, apiKey, appId, os, deviceId))

      val in = Iteratee.foreach[String] {
        input => onInput(input)
      }

      val out: Enumerator[String] = Concurrent.unicast(c => {
        channel = Some(c)

        // start a timeout to close the connection after a while
        Promise.timeout({
          Logger.info(s"$self, timeout expired. Closing connection with client at ${client.foreach(_.remoteAddress)}.")
          closeClientConnection()
        }, Timeouts.maxConnectionLifetime)
      })

      val wsLink = (in, out)
      sender ! wsLink

    case Matched(matchee, others, groupUniqueId) =>
      // the assumption is that the info we got is valid
      Logger.info(s"$self, matched. Group id: $groupUniqueId, myself: $matchee, others: $others")
      myself = Some(matchee)
      matchees = Some(others)
      groupId = Some(groupUniqueId)
      hasBeenMatched = true
      val jsonToSend = JsonResponseHelper.createMatchedResponse(matchee, others, groupUniqueId)
      sendToClient(jsonToSend)

    case MatcheeLeftGroup(matchee, reason) =>
      if (groupId.isDefined) {
        Logger.info(s"$self, matchee left group: ${groupId.get}, matchee: $matchee, reason: $reason")
        val message = JsonMessageHelper.createMatcheeLeftGroupMessage(groupId.get, matchee.idInGroup)
        sendToClient(message)

        // TODO: this needs to be improved, the client might stay in the group.
        leaveGroup()
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

  def onMatchInput(matchRequest: ClientInputMessageMatch) = {
    val areaStart = Areas.getAreaFromString(matchRequest.areaStart)
    val areaEnd = Areas.getAreaFromString(matchRequest.areaEnd)
    val criteria = Criteria.getCriteriaFromString(matchRequest.criteria)
    val swipeOrientation = matchRequest.swipeOrientation

    val testValidity = Try(RequestValidityHelper.matchRequestIsValid(criteria, areaStart, areaEnd, swipeOrientation))

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
            matchRequest.equalityParam, matchRequest.orientation, swipeOrientation, self)

          def futureMatched(matcher: ActorRef) = {
            val matchedFuture = (matcher ? NewRequest(requestData))(Timeouts.maxOldestRequestInterval)
            matchedFuture recover {
              // basically this timeout will always trigger, but maybe we've been matched in the meantime.
              case t: TimeoutException => if (!hasBeenMatched) sendToClient(JsonResponseHelper.getTimeoutResponse)
            }
          }

          criteria match {
            // we only get here if the criteria is valid
            case Criteria.POSITION => futureMatched(ApplicationWS.positionMatchingActor)
            case Criteria.PRESENCE => futureMatched(ApplicationWS.touchMatchingActor)
            case Criteria.PINCH => futureMatched(ApplicationWS.pinchMatchingActor)
            case Criteria.AIM => futureMatched(ApplicationWS.aimMatchingActor)
          }
        }

    }
  }

  def onDisconnectInput(disconnect: ClientInputMessageDisconnect) = {
    // a disconnect message doesn't have a groupId
    Logger.info(s"$self, disconnect input, reason: ${disconnect.reason}")
    sendToClient(JsonResponseHelper.getDisconnectResponse)
    if (matchees.isDefined && myself.isDefined) {
      sendMessageToMatchees(MatcheeLeftGroup(myself.get, disconnect.reason))
    }
    closeClientConnection()
    leaveGroup()
  }

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

  def onDeliveryInput(clientDelivery: ClientInputMessageDelivery) = {
    def getDeliveryLog(msg: String = "") = s"$self, client delivery," +
      s" $msg, groupId: ${clientDelivery.groupId}, recipients: ${clientDelivery.recipients} " +
      s", payload length: ${clientDelivery.payload.length}"

    // if not valid, this input will be simply discarded
    if (groupsAreValid(clientDelivery.groupId) && myself.isDefined && matchees.isDefined) {

      // creates a list of recipients
      val listRecipients: List[ActorRef] = for {
        matchee <- matchees.get
        recipient <- clientDelivery.recipients
        if matchee.idInGroup == recipient && recipient != -1
      } yield matchee.handlingActor

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

    }

    else {
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
