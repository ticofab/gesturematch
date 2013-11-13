package actors

import akka.actor.{Actor, Props}
import play.api.Logger
import play.api.libs.json.Json
import consts.Timeouts
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import helpers.JsonResponseHelper

/**
 * This actor will simply pass the content to the actors of the other matched requests.
 */
class ContentExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => {
      this.channel = channel

      // start a timeout to close the connection after a while
      Promise.timeout(channel.foreach(x => x.eofAndEnd()), Timeouts.maxOldestRequestInterval)
    }

    case Matched(myPosition, myPayload, otherInfo) => {
      Logger.info(s"$self, Matched2() message: $myPosition\n  my payload: $myPayload\n")

      otherInfo.length match {
        // there is only another request
        case 1 => {
          val jsonToSend = JsonResponseHelper.getMatched2ContentResponse(otherInfo)
          channel.foreach(x => {
            x.push(Json.stringify(jsonToSend))
            x.eofAndEnd()
          })
        }

        // there are 3 other requests
        case 3 => {
          val jsonToSend = JsonResponseHelper.getMatched4ContentResponse(otherInfo)
          channel.foreach(x => {
            x.push(Json.stringify(jsonToSend))
            x.eofAndEnd()
          })
        }
      }
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // don't do anything with it.
    }

  }
}

object ContentExchangeActor {
  def props: Props = Props(classOf[ContentExchangeActor])
}
