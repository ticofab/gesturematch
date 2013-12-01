package actors.websocket

import akka.actor.{Actor, Props}
import play.api.Logger
import consts.Timeouts
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import helpers.{JsonInputHelper, JsonResponseHelper}
import actors._
import actors.Setup
import models.MatcheeInfo
import actors.Input
import scala.Some
import scala.util.{Success, Failure, Try}
import models.ClientInputMessages.{ClientInputMessageDisconnect, BreakMatchInputMessage}

/**
 * This actor will simply pass the content to the actors of the other matched requests.
 */
class ContentExchangeActorWS extends HandlingActorWS {
  def receive: Actor.Receive = {
    case Setup(c) => {
      this.channel = c

      // start a timeout to close the connection after a while
      Promise.timeout(channel.foreach(x => x.eofAndEnd()), Timeouts.maxOldestRequestInterval)
    }

    case Matched(info: MatcheeInfo, othersInfo: List[MatcheeInfo]) => {
      myInfo = Some(info)
      matcheesInfo = Some(othersInfo)

      Logger.info(s"$self, MatchedDetail message. myInfo: $myInfo, others: $matcheesInfo")

      sendMatchedResponseToClient()
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // try to parse it to Json
      Try(JsonInputHelper.parseInput(input)) match {
        case Success(parsedMessage) => {
          // successfully parsed
          parsedMessage match {
            case disconnect: ClientInputMessageDisconnect => onDisconnectMsg(disconnect)
            case break: BreakMatchInputMessage => {} // TODO
            case _ => {} // TODO: error
          }
        }

        case Failure(e) => {
          // couldn't parse the input message
          // TODO
        }
      }
    }
  }

  def sendMatchedResponseToClient() = {
    if (!myInfo.isEmpty && !matcheesInfo.isEmpty) {
      val jsonToSend = JsonResponseHelper.createMatchedResponse(myInfo.get, matcheesInfo.get)
      channel.foreach(x => {
        x.push(jsonToSend)
      })
    }
  }

  def sendMessageToMatchees(message: MatcheeMessage) = {
    matcheesInfo match {
      case Some(matcheeList) => matcheeList.foreach(matchee => matchee.handlingActor ! message)
      case None => {} // do nothing
    }
  }

  def onDisconnectMsg(disconnect: ClientInputMessageDisconnect) = {
    channel.foreach(x => x.eofAndEnd()) // TODO: send ack message before this
    disconnect.reason match {
      case Some(reason) => sendMessageToMatchees(MatcheeDisconnected(Some(reason)))
      case None => sendMessageToMatchees(MatcheeDisconnected(None))
    }
  }

  def onBreakConnectionMsg(break: BreakMatchInputMessage) = {
    // send messages to the other ones in the connection and simply forget about them
    break.reason match {
      case Some(reason) => sendMessageToMatchees(MatcheeBrokeConnection(Some(reason)))
      case None => sendMessageToMatchees(MatcheeBrokeConnection(None))
    }
    matcheesInfo = None
    // TODO: send ack message to client
  }
}

object ContentExchangeActorWS {
  def props: Props = Props(classOf[ContentExchangeActorWS])
}
