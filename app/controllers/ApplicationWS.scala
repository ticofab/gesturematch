package controllers

import play.api.mvc._
import scala.concurrent.duration.DurationInt
import play.api.libs.iteratee.{Iteratee, Enumerator}
import akka.actor.ActorRef
import akka.pattern.ask
import actors.ClientConnected
import play.api.libs.concurrent.Akka
import play.api.Play.current
import actors.websocket.ContentExchangeActorWS

object ApplicationWS extends MyController {


  def openWS(): WebSocket[String] = WebSocket.async {
    request => {
      val handlingActor: ActorRef = Akka.system.actorOf(ContentExchangeActorWS.props)
      val wsLinkFuture = (handlingActor ? ClientConnected())(5.seconds)
      wsLinkFuture.mapTo[(Iteratee[String, _], Enumerator[String])]
    }
  }
}
