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

import actors._
import models.messages.actors.ConnectedClient
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc._

object ApplicationWS extends Controller {
  Logger.info("******* Server starting. Creating ActorSystem. ********")
  val swipeMatchingActor = Akka.system.actorOf(SwipeMatcherActor.props)
  val pinchMatchingActor = Akka.system.actorOf(PinchMatcherActor.props)
  val universalMatchingActor = Akka.system.actorOf(UniversalMatcherActor.props)

  // TODO: putting actor here in objects makes it hard to test. I can use the Global object.

  def openv1(os: String, deviceId: String) = WebSocket.acceptWithActor[String, String] {
    request => out => ContentExchangeActor.props(ConnectedClient(out, request.remoteAddress, deviceId, os))
  }

}

