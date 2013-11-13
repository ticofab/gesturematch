package actors

import akka.actor.{Props, Actor}

class PhotoExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => this.channel = channel
    case Matched(myPosition, myPayload, otherInfo) => {} // TODO
    case Input(input) => {} // TODO
  }
}

object PhotoExchangeActor {
  def props: Props = Props(classOf[PhotoExchangeActor])
}

