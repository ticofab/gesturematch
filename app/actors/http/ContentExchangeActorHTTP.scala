package actors.http

import akka.actor.{ActorRef, Props, Actor}
import actors.{NewRequest, Matched}
import play.api.Logger
import helpers.JsonResponseHelper
import models.RequestToMatch

class ContentExchangeActorHTTP extends Actor {
  var myDad: Option[ActorRef] = None

  def receive: Actor.Receive = {

    case Matched(myPosition, myPayload, otherInfo) => {
      Logger.info(s"$self, Matched message: $myPosition\n  my payload: $myPayload\n")

      otherInfo.length match {
        // there is only another request
        case 1 => {
          val jsonToSend = JsonResponseHelper.getMatched2ContentResponse(otherInfo)
          Logger.info(s"sending to $myDad")
          myDad.foreach(_ ! jsonToSend)
        }

        // there are 3 other requests
        case 3 => {
          val jsonToSend = JsonResponseHelper.getMatched4ContentResponse(otherInfo)
          myDad.foreach(_ ! jsonToSend)
        }
      }
    }

    case Match(request: RequestToMatch, matcher: ActorRef) => {
      myDad = Some(sender)
      matcher ! NewRequest(request)
    }
  }
}

object ContentExchangeActorHTTP {
  def props: Props = Props(classOf[ContentExchangeActorHTTP])
}

// TODO: create a "super" actor like HandlingActorHTTP and put this case class there, together with myDad reference
case class Match(request: RequestToMatch, matcher: ActorRef)
