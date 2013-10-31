package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.iteratee.{Concurrent, Iteratee, Enumerator}
import akka.actor.{ActorRef, ActorSystem}
import actors._
import models.RequestToMatch
import actors.Setup
import actors.Input
import scala.Some
import play.api.Logger

object Application extends Controller {
  val system = ActorSystem("screens-system")
  val matchingActor = system.actorOf(MatcherActor.props)

  def requestWS(requestType: String,
                latitude: Double,
                longitude: Double,
                swipeStart: Int,
                swipeEnd: Int,
                payload: String,
                equalityParam: String): WebSocket[String] = WebSocket.async {

    def isRequestValid = {
      Logger.info(s"New request: $latitude $longitude $swipeStart $swipeEnd $equalityParam\n    $payload")

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
        Logger.info("    Request valid.")
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
        val requestData = new RequestToMatch(latitude, longitude, timestamp, swipeStart,
          swipeEnd, equalityParam, payload, handlingActor)

        matchingActor ! NewRequest(requestData)

        (in, out)
      } else {
        // we don't like this request.
        Logger.info(s"    Request invalid. Closing socket.")
        val in: Iteratee[String, Unit] = Iteratee.ignore[String]
        val out: Enumerator[String] = Enumerator.eof
        (in, out)
      }
    }
  }
}