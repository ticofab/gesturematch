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
import consts.{RequestTypes, Criteria, Areas}
import scala.util.{Success, Failure, Try}

object ApplicationWS extends MyController {

  def requestWS(`type`: String,
                criteria: String,
                apiKey: String,
                appId: String,
                latitude: Double,
                longitude: Double,
                areaStart: String,
                areaEnd: String,
                deviceId: String,
                equalityParam: String): WebSocket[String] = WebSocket.async {

    request => Future {
      Logger.info(s"requestWS API call. Request:\n  ${request.remoteAddress} ${request.version} ${request.method} ${request.uri}")

      val typeValue = RequestTypes.getTypeFromString(`type`)
      val areaStartValue = Areas.getAreaFromString(areaStart)
      val areaEndValue = Areas.getAreaFromString(areaEnd)
      val criteriaValue = Criteria.getCriteriaFromString(criteria)

      val testValidity = Try(RequestAnalyticsHelper.requestIsValid(typeValue, criteriaValue, areaStartValue, areaEndValue))
      testValidity match {

        case Failure(e) => {
          // request issue
          Logger.info(s"    Request invalid. Closing socket with: " + e.getMessage)
          val in: Iteratee[String, Unit] = Iteratee.ignore[String]
          val out: Enumerator[String] = Enumerator(e.getMessage).andThen(Enumerator.eof)
          (in, out)
        }

        case Success(isValid) => {
          Logger.info("    Request valid.")
          val props = HandlingActorFactory.getActorProps(typeValue)

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
          val movement = SwipeMovementHelper.swipesToMovement(areaStartValue, areaEndValue)
          val requestData = new RequestToMatch(apiKey, appId, deviceId, latitude, longitude,
            timestamp, areaStartValue, areaEndValue, movement, equalityParam, handlingActor)

          criteriaValue match {
            // we only get here if the criteria is valid
            case Criteria.POSITION => MyController.positionMatchingActor ! NewRequest(requestData)
            case Criteria.PRESENCE => MyController.touchMatchingActor ! NewRequest(requestData)
          }

          (in, out)
        }
      }
    }
  }
}
