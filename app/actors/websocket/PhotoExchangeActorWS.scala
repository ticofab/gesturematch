package actors.websocket

import akka.actor.{Props, Actor}
import actors.{Setup, Input, Matched}

class PhotoExchangeActorWS extends HandlingActorWS {
  def receive: Actor.Receive = {
    case Setup(channel) => this.channel = channel
    case Matched(myPosition, myPayload, otherInfo) => {} // TODO
    case Input(input) => {} // TODO
  }
}

object PhotoExchangeActorWS {
  def props: Props = Props(classOf[PhotoExchangeActorWS])
}

