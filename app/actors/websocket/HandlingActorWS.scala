package actors.websocket

import play.api.libs.iteratee.Concurrent
import akka.actor.Actor

/**
 * Common stuff for the WebSocket actors
 */
trait HandlingActorWS extends Actor {
  var channel: Option[Concurrent.Channel[String]] = None
}
