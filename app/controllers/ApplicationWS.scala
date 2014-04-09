/*
 * Copyright 2014 Fabio Tiriticco, Fabway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import helpers.storage.DBHelper

object ApplicationWS extends Controller {
  Logger.info("******* Server starting. Creating ActorSystem. ********")
  val swipeMatchingActor = Akka.system.actorOf(SwipeMatcherActor.props)
  val pinchMatchingActor = Akka.system.actorOf(PinchMatcherActor.props)

  // TODO: putting actor here in objects makes it hard to test. I can use the Global object.

  // Endpoint to open the WebSocket connection.
  def openv1(apiKey: String, appId: String, os: String, deviceId: String): WebSocket[String] = WebSocket.async {
    request => {
      Logger.info(s"open websocket endpoint connection: $request")

      val testValidity: Future[Boolean] = DBHelper.areKeyAndIdValid(apiKey, appId)
      testValidity.flatMap {
        isValid => {
          if (isValid) {
            // request valid
            Logger.debug(s"apiKey and appId: ($apiKey, $appId) are valid.")
            val handlingActor: ActorRef = Akka.system.actorOf(ContentExchangeActor.props)
            val connectionMsg = ConnectedClient(request.remoteAddress, apiKey, appId, os, deviceId)
            val wsLinkFuture = (handlingActor ? connectionMsg)(Timeouts.maxOldestRequestInterval)
            wsLinkFuture.mapTo[(Iteratee[String, _], Enumerator[String])].recover {
              case e: TimeoutException =>
                // no actor responded.
                // this is the recover of a Future so this will be a Future! No need for "Future { }"
                Logger.error(s"open websocket endpoint, no actor responded. Close connection, exception: $e")
                val out = Enumerator(JsonResponseHelper.getServerErrorResponse).andThen(Enumerator.eof)
                val in: Iteratee[String, Unit] = Iteratee.ignore
                (in, out)
            }
          } else {
            Future {
              Logger.debug(s"apiKey and appId: ($apiKey, $appId) are NOT valid.")
              val out = Enumerator(s"ApiKey and AppId pair ($apiKey, $appId) is not valid.").andThen(Enumerator.eof)
              val in: Iteratee[String, Unit] = Iteratee.ignore
              (in, out)
            }
          }
        }
      } recover {
        // this is the recover of a Future so this will be a Future! No need for "Future { }"
        case t: TimeoutException => {
          // database timeout.
          Logger.debug(s"Database TimeoutException: $t")
          val exceptionMsg = s"Database didn't respond within ${Timeouts.maxDatabaseResponseTime.toString()}"
          val out = Enumerator(exceptionMsg).andThen(Enumerator.eof)
          val in: Iteratee[String, Unit] = Iteratee.ignore
          (in, out)
        }
      }
    }
  }
}
