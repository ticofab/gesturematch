package actors.websocket

import play.api.libs.iteratee.Concurrent
import akka.actor.{ActorRef, Actor}
import consts.MatcheeInfo

/**
 * Common stuff for the WebSocket actors
 */
trait HandlingActorWS extends Actor {
  var channel: Option[Concurrent.Channel[String]] = None
  var matches: Option[List[MatcheeInfo]] = None
}
