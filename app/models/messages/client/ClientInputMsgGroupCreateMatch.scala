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

package models.messages.client

import consts.json.JsonInputLabels
import models.messages.client.base.{ClientInputMatchRequest, ClientInputMsg}
import play.api.libs.json.Reads._
import play.api.libs.json._

case class ClientInputMsgGroupCreateMatch(override val criteria: String,
                                          override val areaStart: String,
                                          override val areaEnd: String,
                                          override val equalityParam: Option[String],
                                          latitude: Double,
                                          longitude: Double) extends ClientInputMatchRequest(criteria, areaStart, areaEnd, equalityParam) with ClientInputMsg

object ClientInputMsgGroupCreateMatch {

  def fromJson(jsonValue: JsValue): ClientInputMsgGroupCreateMatch = {
    val criteria = (jsonValue \ JsonInputLabels.MATCH_INPUT_CRITERIA).as[String]
    val latitude = (jsonValue \ JsonInputLabels.MATCH_INPUT_LATITUDE).as[Double]
    val longitude = (jsonValue \ JsonInputLabels.MATCH_INPUT_LONGITUDE).as[Double]
    val areaStart = (jsonValue \ JsonInputLabels.MATCH_INPUT_AREASTART).as[String]
    val areaEnd = (jsonValue \ JsonInputLabels.MATCH_INPUT_AREAEND).as[String]
    val equalityParam = (jsonValue \ JsonInputLabels.MATCH_INPUT_EQUALITYPARAM).asOpt[String]

    // if we got here, it means that everything is fine
    ClientInputMsgGroupCreateMatch(criteria, areaStart, areaEnd, equalityParam, latitude, longitude)
  }

}




