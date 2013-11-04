package actors

import akka.actor.{Props, Actor}
import play.api.Logger

class ContactExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => this.channel = channel

    case Matched2(myPosition, myPayload, otherInfo) => {
      Logger.info(s"$self, Matched2() message: $myPosition\n  my payload: $myPayload\n  others    : ${otherInfo._2}")

      channel.foreach(_.push(s"gotcha, you're $myPosition, your payload is $myPayload and the other's info is ${otherInfo._2}"))
    }

    case Matched4(myPosition, myPayload, otherPayloads) => {
      Logger.info(s"$self, Matched4() message: $myPosition\n  my payload: $myPayload")
      channel.foreach(_.push(s"gotcha, you're $myPosition, your payload is $myPayload"))
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")

      // simple echoing back for now
      channel.foreach(_.push(s"Thanks for $input"))
    }
  }
}

object ContactExchangeActor {
  def props: Props = Props(classOf[ContactExchangeActor])
}
