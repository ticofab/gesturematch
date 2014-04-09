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
import consts.SwipeMovements.SwipeMovement
import consts.Areas.Areas

class RequestToMatch(val apiKey: String,
                     val appId: String,
                     val deviceId: String,
                     val latitude: Double,
                     val longitude: Double,
                     val timestamp: Long,
                     val areaStart: Areas,
                     val areaEnd: Areas,
                     val movement: SwipeMovement,
                     val equalityParam: Option[String],
                     val handlingActor: ActorRef) {
  override def toString: String = s"Request: apiKey $apiKey, appId $appId, " +
    s"deviceId $deviceId, latitude $latitude, longitude $longitude, " +
    s"timestamp $timestamp, areaStart $areaStart, areaEnd $areaEnd, " +
    s"movement $movement, equalityParam $equalityParam, " +
    s"handlingActor $handlingActor"
}
