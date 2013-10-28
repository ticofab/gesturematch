package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.iteratee.{Concurrent, Iteratee, Enumerator}
import akka.actor.{ActorRef, ActorSystem}
import actors.{Input, Setup, HandlingActorFactory}

object Application extends Controller {
  val system = ActorSystem("screens-system")

  def requestWS(requestType: String) = WebSocket.async {
    val props = HandlingActorFactory.getActorProps(requestType) getOrElse (throw new IllegalArgumentException)

    request => Future {
      // setup handling actor
      val handlingActor: ActorRef = system.actorOf(props)
      var channel: Option[Concurrent.Channel[String]] = None
      val out: Enumerator[String] = Concurrent.unicast(c => channel = Some(c))

      handlingActor ! Setup(out)
      val in = Iteratee.foreach[String] {
        input => handlingActor ! Input(input)
      }

      // add it to the matcher queue


      (in, out)
    }
  }
}