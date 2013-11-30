package actors.websocket

import play.api.libs.iteratee.Concurrent
import akka.actor.Actor
import models.MatcheeInfo

/**
 * Common stuff for the WebSocket actors
 */
trait HandlingActorWS extends Actor {
  var channel: Option[Concurrent.Channel[String]] = None
  var matcheesInfo: Option[List[MatcheeInfo]] = None
  var myInfo: Option[MatcheeInfo] = None
}
