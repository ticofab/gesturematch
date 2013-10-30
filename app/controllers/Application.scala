package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.iteratee.{Concurrent, Iteratee, Enumerator}
import akka.actor.{ActorRef, ActorSystem}
import actors.{Input, Setup, HandlingActorFactory}
import models.RequestToMatch

object Application extends Controller {
  val system = ActorSystem("screens-system")

  def requestWS(requestType: String,
                latitude: Double,
                longitude: Double,
                swipeStart: Int,
                swipeEnd: Int,
                equalityParam: String) = WebSocket.async {


    def isRequestValid = {
      // TODO: check more things about parameters
      val validRequestType = requestType match {
        case HandlingActorFactory.PHOTO => true
        case HandlingActorFactory.CONTACT => true
        case _ => false
      }
      val differentSwipes = swipeEnd != swipeStart
      validRequestType && differentSwipes
    }

    request => Future {
      if (isRequestValid) {
        val props = HandlingActorFactory.getActorProps(requestType)

        // setup handling actor
        val handlingActor: ActorRef = system.actorOf(props)
        var channel: Option[Concurrent.Channel[String]] = None
        val out: Enumerator[String] = Concurrent.unicast(c => channel = Some(c))

        handlingActor ! Setup(out)
        val in = Iteratee.foreach[String] {
          // pass everything the client sends to its managing actor
          input => handlingActor ! Input(input)
        }

        // add it to the matcher queue
        val timestamp = System.currentTimeMillis
        val requestData = new RequestToMatch(latitude, longitude, timestamp, swipeStart, swipeEnd, equalityParam, handlingActor)


        (in, out)
      } else {
        // we don't like this request.
        val in: Iteratee[String, Unit] = Iteratee.ignore[String]
        val out = Enumerator.eof
        (in, out)
      }
    }
  }
}