package actors

import akka.actor.{ActorRef, Actor, Props}
import akka.pattern.ask
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import helpers.RequestAnalyticsHelper
import models._
import scala.util.Try
import models.ClientInputMessages._
import consts.{Timeouts, Areas, Criteria}
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import models.NewRequest
import scala.util.Failure
import scala.Some
import scala.util.Success
import play.api.libs.concurrent.Promise
import helpers.json.{JsonMessageHelper, JsonResponseHelper, JsonInputHelper}
import helpers.movements.SwipeMovementHelper
import controllers.ApplicationWS
import consts.json.{JsonResponseLabels, JsonGeneralLabels}
import java.util.concurrent.TimeoutException

class ContentExchangeActor extends Actor {
  var remoteIPAddress: Option[String] = None
  var channel: Option[Concurrent.Channel[String]] = None
  var matchees: Option[List[Matchee]] = None
  var myInfo: Option[Matchee] = None
  var groupId: Option[String] = None

  def receive: Actor.Receive = {
    case ClientConnected(remoteAddress) => {
      Logger.info(s"ClientConnected: $remoteAddress managed by $self")
      remoteIPAddress = Some(remoteAddress)

      val in = Iteratee.foreach[String] {
        input => onInput(input)
      }

      val out: Enumerator[String] = Concurrent.unicast(c => {
        channel = Some(c)

        // start a timeout to close the connection after a while
        Promise.timeout({
          Logger.info(s"$self, timeout expired. Closing connection with client at ${remoteIPAddress.getOrElse("<IP unavailable>")}.")
          closeClientConnection()
        }, Timeouts.maxConnectionLifetime)
      })

      val wsLink = (in, out)
      sender ! wsLink
    }

    case Matched(info: Matchee, others: List[Matchee], groupUniqueId: String) => {
      Logger.info(s"$self, MatchedDetail message. my matchee: $info, others: $others")
      myInfo = Some(info)
      matchees = Some(others)
      groupId = Some(groupUniqueId)
      sendMatchedResponseToClient()
    }

    case MatcheeLeftGroup(matchee, reason) => {
      Logger.info(s"$self, a matchee left the group.")
      sendMatcheeLeftMessageToClient(matchee, reason)
      leaveGroup()
    }

    case MatcheeDelivers(matchee, payload) => {
      Logger.info(s"$self, a matchee delivered some payload (length: ${payload.length}). Forwarding it to my client.")
      val payloadMsg = JsonMessageHelper.createMatcheeSendsPayloadMessage(matchee.idInGroup, payload)
      sendToClient(payloadMsg)
    }
  }

  // *************************************
  // Events section
  // *************************************
  def onInput(input: String) = {

    if (input.contains(JsonGeneralLabels.PAYLOAD)) {
      Logger.info(s"$self, input message from client. Length: ${input.length}, Input: ${input.substring(0, 100)}")
    } else {
      Logger.info(s"$self, input message from client. Length: ${input.length}, Input: $input")
    }
    // try to parse it to Json
    Try(JsonInputHelper.parseInput(input)) match {

      case Success(parsedMessage) => {

        // successfully parsed
        parsedMessage match {
          case disconnect: ClientInputMessageDisconnect => onDisconnectInput(disconnect)
          case matchRequest: ClientInputMessageMatch => onMatchInput(matchRequest)
          case leaveGroup: ClientInputMessageLeaveGroup => onLeaveGroupInput(leaveGroup)
          case delivery: ClientInputMessageDelivery => onDeliveryInput(delivery)
          case _ => sendInvalidInputResponseToClient() // TODO: error
        }
      }

      case Failure(e) => {
        // TODO: more descriptive error
        lazy val invalidJson = "Error parsing the JSON input."
        Logger.info(s"Couldn't parse client message: ${e.getMessage}")
        sendInvalidInputResponseToClient(Some(invalidJson))
      }
    }
  }

  def onMatchInput(matchRequest: ClientInputMessageMatch) = {

    val areaStartValue = Areas.getAreaFromString(matchRequest.areaStart)
    val areaEndValue = Areas.getAreaFromString(matchRequest.areaEnd)
    val criteriaValue = Criteria.getCriteriaFromString(matchRequest.criteria)

    val testValidity = Try(RequestAnalyticsHelper.requestIsValid(criteriaValue, areaStartValue, areaEndValue))

    testValidity match {

      case Failure(e) => {
        // request issue
        Logger.info(s"    Request invalid, reason: ${e.getMessage}")
        sendToClient(JsonResponseHelper.getInvalidMatchRequestResponse)
      }

      case Success(isValid) => {
        Logger.info("    Request valid.")

        // add request to the matcher queue
        val timestamp = System.currentTimeMillis
        val movement = SwipeMovementHelper.swipesToMovement(areaStartValue, areaEndValue)
        val requestData = new RequestToMatch(matchRequest.apiKey, matchRequest.appId, matchRequest.deviceId,
          matchRequest.latitude, matchRequest.longitude, timestamp, areaStartValue, areaEndValue, movement,
          matchRequest.equalityParam, self)

        def futureMatched(matcher: ActorRef) = {
          val matchedFuture = (matcher ? NewRequest(requestData))(Timeouts.maxOldestRequestInterval)
          matchedFuture recover {
            // basically this timeout will always trigger. But maybe we've been matched in the meantime
            case t: TimeoutException => if (groupId.isEmpty) sendToClient(JsonResponseHelper.getTimeoutResponse)
          }
        }

        criteriaValue match {
          // we only get here if the criteria is valid
          case Criteria.POSITION => futureMatched(ApplicationWS.positionMatchingActor)
          case Criteria.PRESENCE => futureMatched(ApplicationWS.touchMatchingActor)
        }
      }
    }
  }

  def onDisconnectInput(disconnect: ClientInputMessageDisconnect) = {
    // a disconnect message doesn't have a groupId
    Logger.info(s"$self, client disconnected. Reason: ${disconnect.reason}")
    sendToClient(JsonResponseHelper.getDisconnectResponse)
    if (!matchees.isEmpty) {
      disconnect.reason match {
        // TODO: prevent that we even get here if myInfo is None
        case Some(reason) => sendMessageToMatchees(MatcheeLeftGroup(myInfo.get, Some(reason)))
        case None => sendMessageToMatchees(MatcheeLeftGroup(myInfo.get))
      }
    }
    closeClientConnection()
    leaveGroup()
  }

  def onLeaveGroupInput(leaveGroupMessage: ClientInputMessageLeaveGroup) = {
    // if not valid, this input wiil be simply discarded
    if (isValidInput(leaveGroupMessage.groupId)) {
      Logger.info(s"$self, client asks to leave group ${leaveGroupMessage.groupId}, Reason: ${leaveGroupMessage.reason}")
      // send messages to the other ones in the connection and simply forget about them
      matchees match {
        case Some(matcheesList) => {
          leaveGroupMessage.reason match {
            // TODO: prevent that we even get here if myInfo is None
            case Some(reason) => sendMessageToMatchees(MatcheeLeftGroup(myInfo.get, Some(reason)))
            case None => sendMessageToMatchees(MatcheeLeftGroup(myInfo.get, None))
          }
          sendToClient(JsonResponseHelper.getGroupLeftResponse(groupId.get))
          leaveGroup()
        }
        case None => {
          // we shouldn't get here but just in case
          sendToClient(JsonResponseHelper.getNotPartOfGroupResponse(groupId.get))
        }
      }
    } else {
      Logger.info(s"$self, client is not part of group ${leaveGroupMessage.groupId}")
      sendToClient(JsonResponseHelper.getNoGroupToLeaveResponse)
    }
  }

  def onDeliveryInput(delivery: ClientInputMessageDelivery) = {
    // if not valid, this input wiil be simply discarded
    if (isValidInput(delivery.groupId)) {
      // TODO: prevent that we even get here if myInfo == None or groupId == None!
      Logger.info(s"$self, client delivery, for ${delivery.recipients}, payload length: ${delivery.payload.length}")
      // create a delivery message and deliver it to the right recipients!
      val matcheesList: List[Matchee] = matchees.getOrElse(Nil)
      if (groupId.isEmpty) {
        Logger.info(s"No groups seem to be established")
        sendToClient(JsonResponseHelper.getPayloadEmptyGroupResponse(groupId.get))
      } else {
        val message = MatcheeDelivers(myInfo.get, delivery.payload)
        val listRecipients: List[ActorRef] = for {
          matchee <- matcheesList
          recipient <- delivery.recipients
          if matchee.idInGroup == recipient && recipient != -1
        } yield matchee.handlingActor

        listRecipients.foreach(_ ! message)
        if (listRecipients.size == delivery.recipients.size) {
          Logger.info(s"$self, Delivered to all requested recipients.")
          sendToClient(JsonResponseHelper.getPayloadDeliveredResponse(groupId.get))
        } else {
          Logger.info(s"$self, Delivered to a subset of the recipients.")
          sendToClient(JsonResponseHelper.getPayloadPartiallyDeliveredResponse(groupId.get))
        }
      }
    } else {
      Logger.info(s"$self, client is not part of group ${delivery.groupId}")
      sendToClient(JsonResponseHelper.getPayloadNotDeliveredResponse(delivery.groupId,
        Some(JsonResponseLabels.REASON_NOT_PART_OF_THIS_GROUP)))
    }
  }

  def isValidInput(inputGroupId: String) = {
    // TODO: watch out for the get!
    if (inputGroupId == groupId.getOrElse("")) true
    else {
      sendToClient(JsonResponseHelper.getWrongGroupIdResponse(inputGroupId))
      false
    }
  }

  // *************************************
  // Responses to client section
  // *************************************
  def sendInvalidInputResponseToClient(reason: Option[String] = None) = {
    // TODO: send a more helpful message back
    sendToClient(JsonResponseHelper.getInvalidInputResponse(reason))
  }

  def sendMatchedResponseToClient() = {
    if (!myInfo.isEmpty && !matchees.isEmpty) {
      val jsonToSend = JsonResponseHelper.createMatchedResponse(myInfo.get, matchees.get, groupId.get)
      sendToClient(jsonToSend)
    }
  }

  def sendMatcheeLeftMessageToClient(matchee: Matchee, reason: Option[String]) = {
    val message = JsonMessageHelper.createMatcheeLeavesMessage(matchee.idInGroup)
    sendToClient(message)
  }

  // *************************************
  // General functions section
  // *************************************
  def leaveGroup() = {
    matchees = None
    myInfo = None
    groupId = None
  }

  def sendToClient(message: String) = {
    channel.foreach(x => x.push(message))
  }

  def closeClientConnection() = channel.foreach(x => x.eofAndEnd())

  def sendMessageToMatchees(message: MatcheeMessage) = {
    matchees match {
      case Some(matcheeList) => matcheeList.foreach(matchee => matchee.handlingActor ! message)
      case None => {} // do nothing
    }
  }
}

object ContentExchangeActor {
  def props: Props = Props(classOf[ContentExchangeActor])
}
