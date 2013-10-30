package actors

import akka.actor.{ActorRef, Actor}
import play.api.libs.iteratee.Enumerator
import consts.ScreenPositions.ScreenPosition

trait HandlingActor extends Actor {
  val out: Option[Enumerator[String]] = None
}

case class Setup(out: Enumerator[String])
case class Input(input: String)
case class Matched4(position: ScreenPosition, payload: String, otherPayloads: List[(ActorRef, String)])
case class Matched2(position: ScreenPosition, payload: String, otherPayload: (ActorRef, String))