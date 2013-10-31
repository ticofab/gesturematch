package actors

import akka.actor.{Props, Actor}
import play.api.Logger

class ContactExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => this.channel = channel

    case Matched2(myPosition, myPayload, otherPayload) => {
      Logger.info(s"Actor $self, Matched2() message: $myPosition\n  my payload: $myPayload\n  others   : $otherPayload")

      channel.foreach(_.push(s"gotcha, you're $myPosition"))
    }

    case Matched4(myPosition, myPayload, otherPayloads) => {} // TODO

    case Input(input) => {} // TODO
  }
}

object ContactExchangeActor {
  def props: Props = Props(classOf[ContactExchangeActor])
}