package actors.websocket

import akka.actor.{Props, Actor}
import actors.{Setup, Input, MatchedPosition}

class PhotoExchangeActorWS extends HandlingActorWS {
  def receive: Actor.Receive = {
    case Setup(channel) => this.channel = channel
    case MatchedPosition(myPosition, otherInfo) => {} // TODO
    case Input(input) => {} // TODO
  }
}

object PhotoExchangeActorWS {
  def props: Props = Props(classOf[PhotoExchangeActorWS])
}

