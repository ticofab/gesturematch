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

package models.matching

import akka.actor.ActorRef
import consts.Areas.Areas
import consts.SwipeMovements.SwipeMovement
import models.matching.base.MatchRequest

case class GroupComMatchRequest(override val deviceId: String,
                                override val timestamp: Long,
                                override val areaStart: Areas,
                                override val areaEnd: Areas,
                                override val movement: SwipeMovement,
                                override val equalityParam: Option[String],
                                override val handlingActor: ActorRef,
                                groupId: Option[String],
                                idInGroup: Option[Int])
  extends MatchRequest(deviceId, timestamp, areaStart, areaEnd, movement, equalityParam, handlingActor)
