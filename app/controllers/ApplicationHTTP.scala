package controllers

import play.api.mvc.Action
import akka.pattern.{AskTimeoutException, ask}
import scala.concurrent.Future
import play.api.Logger
import actors.HandlingActorFactory
import akka.actor.ActorRef
import helpers.{RequestAnalyticsHelper, JsonResponseHelper, SwipeMovementHelper}
import models.RequestToMatch
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import actors.http.Match
import play.api.libs.concurrent.Akka
import play.api.Play.current
import consts.Areas

object ApplicationHTTP extends MyController {

  // simple alive response
  def alive() = Action {
    request => Ok("I'm alive!\n")
  }

  /*
  def requestHTTP(`type`: String,
                  criteria: String,
                  apiKey: String,
                  appId: String,
                  latitude: Double,
                  longitude: Double,
                  swipeStart: Int,
                  swipeEnd: Int,
                  deviceId: String,
                  equalityParam: String,
                  payload: String) = Action.async {
    request => {

      Logger.info(s"requestHTTP API call. Request:\n  ${request.remoteAddress} ${request.version} ${request.method} ${request.uri}")
      Logger.info(s"  parameters: $latitude $longitude $swipeStart $swipeEnd $equalityParam $payload\n")

      if (!RequestAnalyticsHelper.requestIsValid(`type`, swipeStart, swipeEnd)) {

        val result = BadRequest("Invalid request!")
        Future.successful(result)

      } else {

        Logger.info("    Request valid.")
        val props = HandlingActorFactory.getActorProps(`type`, HandlingActorFactory.HTTP)

        // setup handling actor
        val handlingActor: ActorRef = Akka.system.actorOf(props)

        // add it to the matcher queue
        val timestamp = System.currentTimeMillis
        val movement = SwipeMovementHelper.swipesToMovement(swipeStart, swipeEnd)
        val requestData = new RequestToMatch(criteria, apiKey, appId, deviceId, latitude, longitude,
          timestamp, Areas.withName(swipeStart.toString), Areas.withName(swipeStart.toString),
          movement, equalityParam, payload, handlingActor)

        val future: Future[String] = (handlingActor ? Match(requestData, MyController.positionMatchingActor))(5.seconds).mapTo[String]

        future.map(result => Ok(result)).recover {
          case ex: AskTimeoutException => Ok(JsonResponseHelper.getTimeoutResponse)
          case _ => Ok(JsonResponseHelper.getUnknownErrorResponse)
        }
      }
    }
  }
  */
}
