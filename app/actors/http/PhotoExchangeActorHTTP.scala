package actors.http

import akka.actor.{Props, Actor}
import actors.{Input, Matched, Setup}

class PhotoExchangeActorHTTP extends Actor {
  def receive = {
    case Setup(channel) => ??? // TODO
    case Matched(myPosition, myPayload, otherInfo) => ??? // TODO
    case Input(input) => ??? // TODO
  }
}

object PhotoExchangeActorHTTP {
  def props: Props = Props(classOf[PhotoExchangeActorHTTP])
}
