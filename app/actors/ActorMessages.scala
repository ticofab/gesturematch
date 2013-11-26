package actors

import akka.actor.ActorRef
import play.api.libs.iteratee.Concurrent
import consts.ScreenPositions.ScreenPosition
import models.RequestToMatch

// matching stuff
case class NewRequest(request: RequestToMatch)

// request handling actors
case class Setup(channel: Option[Concurrent.Channel[String]])
case class Matched(position: ScreenPosition, payload: String, othersInfo: Vector[(ActorRef, String)])
case class Input(input: String)

