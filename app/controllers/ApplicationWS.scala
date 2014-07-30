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

import java.util.concurrent.TimeoutException
import models.database.SessionUser
import models.messages.actors.ConnectedClient
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import actors._
import akka.actor.ActorRef
import helpers.json.JsonErrorHelper
import helpers.storage.DBHelper
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc._

import scala.concurrent.Future

object ApplicationWS extends Controller {
  Logger.info("******* Server starting. Creating ActorSystem. ********")
  val swipeMatchingActor = Akka.system.actorOf(SwipeMatcherActor.props)
  val pinchMatchingActor = Akka.system.actorOf(PinchMatcherActor.props)

  // TODO: putting actor here in objects makes it hard to test. I can use the Global object.

  def openv1(apiKey: String, appId: String, os: String, deviceId: String) = WebSocket.tryAcceptWithActor[String, String] {

    request => {
      Logger.info(s"open websocket endpoint connection: $request")

      val futureUser: Future[Option[SessionUser]] = DBHelper.getSessionUser(apiKey, appId)

      futureUser map {
          case None =>
            Logger.debug(s"apiKey and appId: ($apiKey, $appId) are NOT valid.")
            val errorMsg = JsonErrorHelper.createInvalidCredentialsError(apiKey, appId)
            Left(Forbidden(errorMsg))
          case Some(sessionUser) =>
            Right((out: ActorRef) => {
              val connectedClient = ConnectedClient(request.remoteAddress, deviceId, apiKey, appId, os)
              ContentExchangeActor.props(out, connectedClient, sessionUser)
            })
        } recover {
        // this is the recover of a Future so this will be a Future! No need for "Future { }"
        case t: TimeoutException =>
          // database timeout.
          Logger.error(s"Database TimeoutException: $t")
          val exceptionMsg = JsonErrorHelper.createDatabaseError
          Left(InternalServerError(exceptionMsg))

        case e: Exception =>
          // database timeout.
          Logger.error(s"Future failure: $e")
          val exceptionMsg = s"Database failure."
          Left(InternalServerError(exceptionMsg))
      }

    }
  }
}

