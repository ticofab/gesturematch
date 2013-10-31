package actors

import akka.actor.{ActorRef, Actor}
import play.api.libs.iteratee.Concurrent
import consts.ScreenPositions.ScreenPosition

trait HandlingActor extends Actor {
  var channel: Option[Concurrent.Channel[String]] = None
}

case class Setup(channel: Option[Concurrent.Channel[String]])
case class Input(input: String)
case class Matched4(position: ScreenPosition, payload: String, otherPayloads: List[(ActorRef, String)])
case class Matched2(position: ScreenPosition, payload: String, otherPayload: (ActorRef, String))