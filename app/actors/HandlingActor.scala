package actors

import akka.actor.{ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import consts.ScreenPositions.ScreenPosition

trait HandlingActor extends Actor {
  var channel: Option[Concurrent.Channel[String]] = None
}

case class Setup(channel: Option[Concurrent.Channel[String]])
case class Matched(position: ScreenPosition, payload: String, othersInfo: Vector[(ActorRef, String)])
case class Input(input: String)

