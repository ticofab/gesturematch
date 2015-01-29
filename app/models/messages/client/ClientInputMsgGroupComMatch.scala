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

package models.messages.client

import consts.json.{JsonInputLabels, JsonResponseLabels}
import models.messages.client.base.{ClientInputMsg, ClientInputMatchRequest}
import play.api.libs.json.JsValue

case class ClientInputMsgGroupComMatch(override val criteria: String,
                                       override val areaStart: String,
                                       override val areaEnd: String,
                                       override val equalityParam: Option[String],
                                       groupId: Option[String],
                                       idInGroup: Option[Int]) extends ClientInputMatchRequest(criteria, areaStart, areaEnd, equalityParam) with ClientInputMsg

object ClientInputMsgGroupComMatch {

  def fromJson(jsonValue: JsValue): ClientInputMsgGroupComMatch = {
    val criteria = (jsonValue \ JsonInputLabels.MATCH_INPUT_CRITERIA).as[String]
    val areaStart = (jsonValue \ JsonInputLabels.MATCH_INPUT_AREASTART).as[String]
    val areaEnd = (jsonValue \ JsonInputLabels.MATCH_INPUT_AREAEND).as[String]
    val equalityParam = (jsonValue \ JsonInputLabels.MATCH_INPUT_EQUALITYPARAM).asOpt[String]
    val groupId = (jsonValue \ JsonInputLabels.MATCH_INPUT_GROUPID).asOpt[String]
    val idInGroup = (jsonValue \ JsonResponseLabels.MYSELF_IN_GROUP).asOpt[Int]

    // if we got here, it means that everything is fine
    ClientInputMsgGroupComMatch(criteria, areaStart, areaEnd, equalityParam, groupId, idInGroup)
  }
}

