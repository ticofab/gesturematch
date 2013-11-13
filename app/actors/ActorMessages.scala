package actors

import akka.actor.ActorRef
import play.api.libs.iteratee.Concurrent
import consts.ScreenPositions.ScreenPosition

case class Setup(channel: Option[Concurrent.Channel[String]])
case class Matched(position: ScreenPosition, payload: String, othersInfo: Vector[(ActorRef, String)])
case class Input(input: String)

