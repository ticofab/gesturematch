/*
 * Copyright 2014-2015 Fabio Tiriticco, Fabway
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

package models.messages.actors

import akka.actor.ActorRef
import consts.MatchCriteria.MatchCriteria
import consts.SwipeMovementType.SwipeMovementType
import models.matching.{Delivery, GroupComMatchRequest, GroupCreateMatchRequest, Matchee}
import models.scheme.Scheme

// matching stuff
case class NewCreateRequest(request: GroupCreateMatchRequest)

case class NewComRequest(request: GroupComMatchRequest)

// client handling actor messages
case class ConnectedClient(outActor: ActorRef, remoteAddress: String, deviceId: String, os: String)

case class Matched(criteria: MatchCriteria, movementType: SwipeMovementType, matchees: List[Matchee], groupId: String, scheme: Option[Scheme] = None)

case class MatchedInGroup(criteria: MatchCriteria, movementType: SwipeMovementType, groupMatchees: List[Int])

case class Input(input: String)

// inter actor messages
sealed trait MatcheeMessage

case class MatcheeLeftGroup(matchee: Matchee, reason: Option[String] = None) extends MatcheeMessage

case class MatcheeDelivers(matchee: Matchee, delivery: Delivery) extends MatcheeMessage



