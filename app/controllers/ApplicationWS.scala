package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.iteratee.{Concurrent, Iteratee, Enumerator}
import akka.actor.ActorRef
import actors._
import models.RequestToMatch
import actors.Setup
import actors.Input
import scala.concurrent.ExecutionContext.Implicits.global
import scala.Some
import play.api.Logger
import helpers.{RequestAnalyticsHelper, SwipeMovementHelper}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import consts.Areas

object ApplicationWS extends MyController {

  def requestWS(`type`: String,
                criteria: String,
                apiKey: String,
                appId: String,
                latitude: Double,
                longitude: Double,
                swipeStart: Int,
                swipeEnd: Int,
                deviceId: String,
                equalityParam: String,
                payload: String): WebSocket[String] = WebSocket.async {

    request => Future {
      Logger.info(s"requestWS API call. Request:\n  ${request.remoteAddress} ${request.version} ${request.method} ${request.uri}")
      Logger.info(s"  parameters: $latitude $longitude $swipeStart $swipeEnd $equalityParam $payload\n")

      if (RequestAnalyticsHelper.requestIsValid(`type`, swipeStart, swipeEnd)) {
        Logger.info("    Request valid.")
        val props = HandlingActorFactory.getActorProps(`type`, HandlingActorFactory.WEBSOCKET)

        // setup handling actor
        val handlingActor: ActorRef = Akka.system.actorOf(props)
        var channel: Option[Concurrent.Channel[String]] = None
        val out: Enumerator[String] = Concurrent.unicast(c => {
          channel = Some(c)
          handlingActor ! Setup(channel)
        })

        val in = Iteratee.foreach[String] {
          // pass everything the client sends to its managing actor
          input => handlingActor ! Input(input)
        }

        // add it to the matcher queue
        val timestamp = System.currentTimeMillis
        val movement = SwipeMovementHelper.swipesToMovement(swipeStart, swipeEnd)
        val requestData = new RequestToMatch(criteria, apiKey, appId, deviceId, latitude, longitude,
          timestamp, Areas.withName(swipeStart.toString), Areas.withName(swipeStart.toString),
          movement, equalityParam, payload, handlingActor)

        MyController.matchingActor ! NewRequest(requestData)

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
