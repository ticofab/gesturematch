/*
 * Copyright 2014-2016 Fabio Tiriticco, Fabway
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

import javax.inject.Inject

import actors.ContentExchangeActor
import akka.actor.ActorRef
import com.google.inject.name.Named
import injection.InjectionModule
import models.messages.actors.ConnectedClient
import play.api.Logger
import play.api.Play.current
import play.api.mvc.{Controller, WebSocket}

class ApplicationWS @Inject()(@Named(InjectionModule.PINCH_MATCHING_ACTOR_NAME) pinchMatcherActor: ActorRef,
                              @Named(InjectionModule.SWIPE_MATCHING_ACTOR_NAME) swipeMatcherActor: ActorRef,
                              @Named(InjectionModule.UNIVERSAL_MATCHING_ACTOR_NAME) universalMatcherActor: ActorRef) extends Controller {

  Logger.info("******* Server starting. Creating ActorSystem. ********")

  def openv1(deviceId: String) = WebSocket.acceptWithActor[String, String] {
    request => out => ContentExchangeActor.props(
      ConnectedClient(out, request.remoteAddress, deviceId),
      swipeMatcherActor,
      pinchMatcherActor,
      universalMatcherActor)
  }

}

