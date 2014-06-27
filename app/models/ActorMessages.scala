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

package models

import akka.actor.ActorRef

// matching stuff
case class NewRequest(request: RequestToMatch)

// client handling actor messages
case class ConnectedClient(outActor: ActorRef, remoteAddress: String, deviceId: String)
case class Matched(matchees: List[Matchee], groupId: String, scheme: Option[Scheme] = None)
case class Input(input: String)

// inter actor messages
sealed trait MatcheeMessage
case class MatcheeLeftGroup(matchee: Matchee, reason: Option[String] = None) extends MatcheeMessage
case class MatcheeDelivers(matchee: Matchee, delivery: Delivery) extends MatcheeMessage



