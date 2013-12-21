package controllers

import play.api.mvc._
import play.api.libs.iteratee.{Iteratee, Enumerator}
import akka.actor.ActorRef
import akka.pattern.ask
import actors.{PresenceMatchingActor, PositionMatcherActor, ContentExchangeActor}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import models.ClientConnected
import play.api.Logger
import consts.Timeouts
import java.util.concurrent.TimeoutException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import helpers.json.JsonResponseHelper

object ApplicationWS extends Controller {
  Logger.info("******* Server starting. Creating ActorSystem. ********")
  val positionMatchingActor = Akka.system.actorOf(PositionMatcherActor.props)
  val touchMatchingActor = Akka.system.actorOf(PresenceMatchingActor.props)

  /*
   Endpoint to open the WebSocket connection.
   NOTES:
     - I chose not to use WebSocket[JsValue] because this would lead to horrible exceptions
       in case inputs don't come as a valid Json String.
   */
  def open(): WebSocket[String] = WebSocket.async {
    request => {
      Logger.info(s"open websocket endpoint connection: $request")
      val handlingActor: ActorRef = Akka.system.actorOf(ContentExchangeActor.props)
      val wsLinkFuture = (handlingActor ? ClientConnected(request.remoteAddress))(Timeouts.maxOldestRequestInterval)
      wsLinkFuture.mapTo[(Iteratee[String, _], Enumerator[String])].recover {
        case e: TimeoutException => {
          // no actor responded.
          Logger.error(s"open websocket endpoint, no actor responded. Close connection, exception: $e")
          val out = Enumerator(JsonResponseHelper.getServerErrorResponse).andThen(Enumerator.eof)
          val in: Iteratee[String, Unit] = Iteratee.ignore
          (in, out)
        }
      }
    }
  }
}
