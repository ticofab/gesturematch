package controllers

import play.api.mvc._
import scala.concurrent.duration.DurationInt
import play.api.libs.iteratee.{Iteratee, Enumerator}
import akka.actor.ActorRef
import akka.pattern.ask
import actors.{TouchMatchingActor, PositionMatcherActor, ContentExchangeActor}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import models.ClientConnected
import play.api.Logger

object ApplicationWS extends Controller {
  Logger.info("\n******* Server starting. Creating ActorSystem. ********")
  val positionMatchingActor = Akka.system.actorOf(PositionMatcherActor.props)
  val touchMatchingActor = Akka.system.actorOf(TouchMatchingActor.props)

  /*
   Endpoint to open the WebSocket connection.
   NOTES:
     - I chose not to use WebSocket[JsValue] because this would lead to horrible exceptions
       in case inputs don't come as a valid JsonString.
   */
  def openWS(): WebSocket[String] = WebSocket.async {
    request => {
      Logger.info(s"openWS endpoint connection: $request")
      val handlingActor: ActorRef = Akka.system.actorOf(ContentExchangeActor.props)
      val wsLinkFuture = (handlingActor ? ClientConnected(request.remoteAddress))(5.seconds)
      wsLinkFuture.mapTo[(Iteratee[String, _], Enumerator[String])]
    }
  }
}
