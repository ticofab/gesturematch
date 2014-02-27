package controllers

import play.api.mvc._
import play.api.libs.iteratee.{Iteratee, Enumerator}
import akka.actor.ActorRef
import akka.pattern.ask
import actors._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.Logger
import consts.Timeouts
import java.util.concurrent.TimeoutException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import helpers.json.JsonResponseHelper
import models.ConnectedClient
import scala.concurrent.Future
import helpers.requests.RequestValidityHelper
import scala.util.{Success, Failure, Try}

object ApplicationWS extends Controller {
  Logger.info("******* Server starting. Creating ActorSystem. ********")
  val swipeMatchingActor = Akka.system.actorOf(SwipeMatcherActor.props)
  val pinchMatchingActor = Akka.system.actorOf(PinchMatcherActor.props)
  val aimMatchingActor = Akka.system.actorOf(AimMatcherActor.props)

  // Endpoint to open the WebSocket connection.
  def open(apiKey: String, appId: String, os: String, deviceId: String): WebSocket[String] = WebSocket.async {
    request => {
      Logger.info(s"open websocket endpoint connection: $request")

      val testValidity = Try(RequestValidityHelper.connectionRequestIsValid(apiKey, appId))
      testValidity match {

        case Failure(e) =>
          Future {
            val out = Enumerator(e.getMessage).andThen(Enumerator.eof)
            val in: Iteratee[String, Unit] = Iteratee.ignore
            (in, out)
          }

        case Success(isValid) =>
          // request valid
          val handlingActor: ActorRef = Akka.system.actorOf(ContentExchangeActor.props)
          val connectionMsg = ConnectedClient(request.remoteAddress, apiKey, appId, os, deviceId)
          val wsLinkFuture = (handlingActor ? connectionMsg)(Timeouts.maxOldestRequestInterval)
          wsLinkFuture.mapTo[(Iteratee[String, _], Enumerator[String])].recover {
            case e: TimeoutException =>
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
