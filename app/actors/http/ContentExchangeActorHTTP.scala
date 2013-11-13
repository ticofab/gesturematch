package actors.http

import akka.actor.{Props, Actor}
import actors.Matched
import play.api.Logger
import helpers.JsonResponseHelper

class ContentExchangeActorHTTP extends Actor {
  def receive: Actor.Receive = {

    case Matched(myPosition, myPayload, otherInfo) => {
      Logger.info(s"$self, Matched message: $myPosition\n  my payload: $myPayload\n")

      otherInfo.length match {
        // there is only another request
        case 1 => {
          val jsonToSend = JsonResponseHelper.getMatched2ContentResponse(otherInfo)
          Logger.info(s"Sending $jsonToSend to ${context.parent}, sender is $sender")
        }

        // there are 3 other requests
        case 3 => {
          val jsonToSend = JsonResponseHelper.getMatched4ContentResponse(otherInfo)
        }
      }
    }
  }
}

object ContentExchangeActorHTTP {
  def props: Props = Props(classOf[ContentExchangeActorHTTP])
}
