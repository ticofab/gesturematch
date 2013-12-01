package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.iteratee.{Concurrent, Iteratee, Enumerator}
import akka.actor.ActorRef
import actors.Setup
import actors.Input
import scala.concurrent.ExecutionContext.Implicits.global
import scala.Some
import play.api.libs.concurrent.Akka
import play.api.Play.current
import actors.websocket.ContentExchangeActorWS

object ApplicationWS extends MyController {


  def openWS(): WebSocket[String] = WebSocket.async {
    request => Future {

      // setup handling actor
      val handlingActor: ActorRef = Akka.system.actorOf(ContentExchangeActorWS.props)
      var channel: Option[Concurrent.Channel[String]] = None
      val out: Enumerator[String] = Concurrent.unicast(c => {
        channel = Some(c)
        handlingActor ! Setup(channel)
      })

      val in = Iteratee.foreach[String] {
        // pass everything the client sends to its managing actor
        input => handlingActor ! Input(input)
      }

      (in, out)

    }
  }
}
