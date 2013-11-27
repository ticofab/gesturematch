package actors.http

import akka.actor.{Props, Actor}
import actors.{Input, MatchedPosition, Setup}

class PhotoExchangeActorHTTP extends Actor {
  def receive = {
    case Setup(channel) => ??? // TODO
    case MatchedPosition(myPosition, myPayload, otherInfo) => ??? // TODO
    case Input(input) => ??? // TODO
  }
}

object PhotoExchangeActorHTTP {
  def props: Props = Props(classOf[PhotoExchangeActorHTTP])
}
