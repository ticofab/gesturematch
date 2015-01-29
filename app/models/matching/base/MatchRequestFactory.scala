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

package models.matching.base

import akka.actor.ActorRef
import consts.Areas.Areas
import consts.SwipeMovements.SwipeMovement
import models.matching.{GroupComMatchRequest, GroupCreateMatchRequest}
import models.messages.client.base.ClientInputMatchRequest
import models.messages.client.{ClientInputMsgGroupComMatch, ClientInputMsgGroupCreateMatch}

object MatchRequestFactory {
  def getMatchRequest(deviceId: String,
                      timestamp: Long,
                      areaStart: Areas,
                      areaEnd: Areas,
                      movement: SwipeMovement,
                      handlingActor: ActorRef,
                      matchRequestMsg: ClientInputMatchRequest): MatchRequest = {

    matchRequestMsg match {

      case groupCreate: ClientInputMsgGroupCreateMatch =>
        GroupCreateMatchRequest(deviceId, timestamp, areaStart, areaEnd, movement, groupCreate.equalityParam,
          handlingActor, groupCreate.latitude, groupCreate.longitude)

      case groupCom: ClientInputMsgGroupComMatch =>
        GroupComMatchRequest(deviceId, timestamp, areaStart, areaEnd, movement, groupCom.equalityParam,
          handlingActor, groupCom.groupId, groupCom.idInGroup)

    }
  }
}
