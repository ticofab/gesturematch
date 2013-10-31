package actors

import akka.actor.{Props, Actor}
import play.api.Logger

class ContactExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => this.channel = channel

    case Matched2(myPosition, myPayload, otherPayload) => {
      Logger.info(s"$self, Matched2() message: $myPosition\n  my payload: $myPayload\n  others    : ${otherPayload._2}")

      channel.foreach(_.push(s"gotcha, you're $myPosition, your payload is $myPayload and the other's payload is ${otherPayload._2}"))
    }

    case Matched4(myPosition, myPayload, otherPayloads) => {} // TODO

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
    }
  }
}

object ContactExchangeActor {
  def props: Props = Props(classOf[ContactExchangeActor])
}
