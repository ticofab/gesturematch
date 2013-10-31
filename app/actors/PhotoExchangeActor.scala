package actors

import akka.actor.{Props, Actor}

class PhotoExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(out) => this.out = Some(out)

    case Matched2(myPosition, myPayload, otherPayload) => {} // TODO

    case Matched4(myPosition, myPayload, otherPayloads) => {} // TODO

    case Input(input) => {} // TODO
  }
}

object PhotoExchangeActor {
  def props: Props = Props(classOf[PhotoExchangeActor])
}

