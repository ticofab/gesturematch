package actors.websocket

import akka.actor.{Actor, Props}
import play.api.Logger
import consts.{MatcheeInfo, Timeouts}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import helpers.JsonResponseHelper
import actors.{MatchedGroup, Input, MatchedPosition, Setup}

/**
 * This actor will simply pass the content to the actors of the other matched requests.
 */
class ContentExchangeActorWS extends HandlingActorWS {
  def receive: Actor.Receive = {
    case Setup(channel) => {
      this.channel = channel

      // start a timeout to close the connection after a while
      Promise.timeout(channel.foreach(x => x.eofAndEnd()), Timeouts.maxOldestRequestInterval)
    }

    case MatchedPosition(myPosition, myPayload, matcheeInfo) => {
      Logger.info(s"$self, Matched message: $myPosition\n  my payload: $myPayload\n")
      matches = Some(matcheeInfo)

      matcheeInfo.length match {
        // there is only another request
        case 1 => {
          val jsonToSend = JsonResponseHelper.getMatched2ContentResponse(matcheeInfo)
          channel.foreach(x => {
            x.push(jsonToSend)
            x.eofAndEnd()
          })
        }

        // there are 3 other requests
        case 3 => {
          val jsonToSend = JsonResponseHelper.getMatched4ContentResponse(matcheeInfo)
          channel.foreach(x => {
            x.push(jsonToSend)
            x.eofAndEnd()
          })
        }
      }
    }

    case MatchedGroup(group: List[MatcheeInfo]) => {
      // I've been matched in a touch-only group
      Logger.info(s"$self, MatchedGroup message, group size: ${group.size}")
      matches = Some(group)
      channel.foreach(_.eofAndEnd)
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // don't do anything with it.
    }
  }
}

object ContentExchangeActorWS {
  def props: Props = Props(classOf[ContentExchangeActorWS])
}
