package actors

import akka.actor.{Actor, Props}
import play.api.Logger
import views.html.helper.input

/**
 * This actor will simply pass the content to the actors of the other matched requests.
 */
class ContentExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => {
      this.channel = channel
    }

    case Matched2(myPosition, myPayload, otherInfo) => {
      Logger.info(s"$self, Matched2() message: $myPosition\n  my payload: $myPayload\n  others    : ${otherInfo._2}")
      channel.foreach(x => {x.push(s"${otherInfo._2}"); x.eofAndEnd()})
    }

    case Matched4(myPosition, myPayload, otherPayloads) => {
      Logger.info(s"$self, Matched4() message: $myPosition\n  my payload: $myPayload")
      channel.foreach(_.push(s"gotcha, you're $myPosition, your payload is $myPayload"))
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // don't do anything with it.
    }
  }
}

object ContentExchangeActor {
  def props: Props = Props(classOf[ContentExchangeActor])
}
