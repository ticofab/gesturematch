package actors

import akka.actor.{ActorRef, Actor, Props}
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

class ContentExchangeActor extends Actor {
  var remoteIPAddress: Option[String] = None
  var channel: Option[Concurrent.Channel[String]] = None
  var matcheesInfo: Option[List[MatcheeInfo]] = None
  var myInfo: Option[MatcheeInfo] = None

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

    case Matched(info: MatcheeInfo, othersInfo: List[MatcheeInfo]) => {
      Logger.info(s"$self, MatchedDetail message. my info: $info, others: $othersInfo")
      myInfo = Some(info)
      matcheesInfo = Some(othersInfo)
      sendMatchedResponseToClient()
    }

    case MatcheeBrokeConnection(matchee, reason) => {
      Logger.info(s"$self, a matchee broke the matching.")
      sendMatcheeLeftMessageToClient(matchee, reason)
      breakMyMatching()
    }

    case MatcheeDisconnected(matchee, reason) => {
      Logger.info(s"$self, a matchee disconnected. Breaking connection.")
      sendMatcheeLeftMessageToClient(matchee, reason)
      breakMyMatching()
    }

    case MatcheeDelivers(matchee, payload) => {
      Logger.info(s"$self, a matchee delivered some payload. Forwarding it to my client.")
      matchee match {
        case Some(matcheeInfo) => {
          val payloadMsg = JsonMessageHelper.createMatcheeSendsPayloadMessage(matcheeInfo.idInGroup, payload)
          sendToClient(payloadMsg)
        }
        case None => ??? // we shouldn't get here
      }
    }
  }

  // *************************************
  // Events section
  // *************************************
  def onInput(input: String) = {
    Logger.info(s"$self, input message from client: $input")
    // try to parse it to Json
    Try(JsonInputHelper.parseInput(input)) match {

      case Success(parsedMessage) => {

        // successfully parsed
        parsedMessage match {
          case disconnect: ClientInputMessageDisconnect => onDisconnectInput(disconnect)
          case matchRequest: ClientInputMessageMatch => onMatchInput(matchRequest)
          case breakMatching: ClientInputMessageBreakMatch => onBreakConnectionInput(breakMatching)
          case delivery: ClientInputMessageDelivery => onDeliveryInput(delivery)
          case _ => sendInvalidInputResponseToClient() // TODO: error
        }
      }

      case Failure(e) => {
        Logger.info(s"Couldn't parse client message: ${e.getMessage}")
        sendInvalidInputResponseToClient()
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

        criteriaValue match {
          // we only get here if the criteria is valid

          // TODO: where to put these matching actors?
          case Criteria.POSITION => ApplicationWS.positionMatchingActor ! NewRequest(requestData)
          case Criteria.PRESENCE => ApplicationWS.touchMatchingActor ! NewRequest(requestData)
        }
      }
    }
  }

  def onDisconnectInput(disconnect: ClientInputMessageDisconnect) = {
    Logger.info(s"$self, client disconnected. Reason: ${disconnect.reason}")
    sendToClient(JsonResponseHelper.getDisconnectResponse)
    closeClientConnection()
    disconnect.reason match {
      case Some(reason) => sendMessageToMatchees(MatcheeDisconnected(myInfo, Some(reason)))
      case None => sendMessageToMatchees(MatcheeDisconnected(None))
    }
  }

  def onBreakConnectionInput(break: ClientInputMessageBreakMatch) = {
    Logger.info(s"$self, client broke the connection. Reason: ${break.reason}")
    // send messages to the other ones in the connection and simply forget about them
    matcheesInfo match {
      case Some(info) => {
        break.reason match {
          case Some(reason) => sendMessageToMatchees(MatcheeBrokeConnection(myInfo, Some(reason)))
          case None => sendMessageToMatchees(MatcheeBrokeConnection(myInfo, None))
        }
        sendToClient(JsonResponseHelper.getMatchBrokenResponse)
        breakMyMatching()
      }
      case None => {
        sendToClient(JsonResponseHelper.getNothingToBreakResponse)
      }
    }
  }

  def onDeliveryInput(delivery: ClientInputMessageDelivery) = {
    Logger.info(s"$self, client delivery, for ${delivery.recipients}, payload: ${delivery.payload}")
    // create a delivery message and deliver it to the right recipients!
    val matchees: List[MatcheeInfo] = matcheesInfo.getOrElse(Nil)
    if (matchees == Nil) {
      Logger.info(s"No groups seem to be established")
      sendToClient(JsonResponseHelper.getPayloadEmptyGroupdResponse)
    } else {
      val message = MatcheeDelivers(myInfo, delivery.payload)
      val listRecipients: List[ActorRef] = for {
        matchee <- matchees
        recipient <- delivery.recipients
        if matchee.idInGroup == recipient && recipient != -1
      } yield matchee.handlingActor

      if (listRecipients.isEmpty) {
        Logger.info(s"No groups seem to be established")
        sendToClient(JsonResponseHelper.getPayloadNotDeliveredResponse)
      } else {
        listRecipients.foreach(_ ! message)
        if (listRecipients.size == matchees.size) {
          Logger.info(s"Delivered to all recipients.")
          sendToClient(JsonResponseHelper.getPayloadDeliveredResponse)
        } else {
          Logger.info(s"Delivered to a subset of the recipients.")
          sendToClient(JsonResponseHelper.getPayloadPartiallyDeliveredResponse)
        }
      }
    }
  }

  // *************************************
  // responses to client section
  // *************************************
  def sendInvalidInputResponseToClient() = {
    // TODO: send a more helpful message back
    sendToClient(JsonResponseHelper.getInvalidInputResponse)
  }


  def sendMatchedResponseToClient() = {
    if (!myInfo.isEmpty && !matcheesInfo.isEmpty) {
      val jsonToSend = JsonResponseHelper.createMatchedResponse(myInfo.get, matcheesInfo.get)
      sendToClient(jsonToSend)
    }
  }

  def sendMatcheeLeftMessageToClient(matchee: Option[MatcheeInfo], reason: Option[String]) = {
    matchee match {
      case Some(matcheeInfo) => {
        val message = JsonMessageHelper.createMatcheeLeavesConnectionMessage(matcheeInfo.idInGroup)
        sendToClient(message)
      }
      case None => ??? // we shouldn't get here
    }
  }

  // *************************************
  // general functions section
  // *************************************
  def breakMyMatching() = {
    matcheesInfo = None
    myInfo = None
  }

  def sendToClient(message: String) = {
    channel.foreach(x => x.push(message))
  }

  def closeClientConnection() = channel.foreach(x => x.eofAndEnd())

  def sendMessageToMatchees(message: MatcheeMessage) = {
    matcheesInfo match {
      case Some(matcheeList) => matcheeList.foreach(matchee => matchee.handlingActor ! message)
      case None => {} // do nothing
    }
  }
}

object ContentExchangeActor {
  def props: Props = Props(classOf[ContentExchangeActor])
}
