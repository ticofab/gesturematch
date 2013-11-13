package actors.http

import akka.actor.{Props, Actor}
import actors.{Input, Matched, Setup}

class ContentExchangeActorHTTP extends Actor {
  def receive: Actor.Receive = {
    case Setup(channel) => ??? // TODO
    case Matched(myPosition, myPayload, otherInfo) => ??? // TODO
    case Input(input) => ??? // TODO
  }
}

object ContentExchangeActorHTTP {
  def props: Props = Props(classOf[ContentExchangeActorHTTP])
}
