package actors

import akka.actor.{ActorRef, Actor}
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import models.ScreenPosition.ScreenPosition


/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 16:58
 */
trait HandlingActor extends Actor {
  val out: Option[Enumerator[String]] = None
}

case class Setup(out: Enumerator[String])
case class Matched(partner: ActorRef)
case class Input(input: String)
case class Matched4(position: ScreenPosition, payload: String, otherPayloads: List[String])
case class Matched2(position: ScreenPosition, payload: String, otherPayload: String)
