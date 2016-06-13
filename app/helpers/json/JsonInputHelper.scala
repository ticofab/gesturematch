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

package helpers.json

import consts.json.{JsonGeneralLabels, JsonInputLabels}
import models.messages.client._
import models.messages.client.base.ClientInputMsg
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

object JsonInputHelper {
  lazy val myName = JsonInputHelper.getClass.getSimpleName

  def parseInput(input: String): ClientInputMsg = {
    val jsonValue: JsValue = Json.parse(input)

    val inputType = (jsonValue \ JsonGeneralLabels.TYPE).asOpt[String]

    inputType match {
      case Some(header) =>
        Logger.debug(s"$myName, input of type $header.")

        header match {
          case JsonInputLabels.INPUT_TYPE_MATCH_CREATE => ClientInputMsgGroupCreateMatch.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_LEAVE_GROUP => ClientInputMsgLeaveGroup.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_DISCONNECT => ClientInputMsgDisconnect.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_DELIVERY => ClientInputMsgDelivery.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_MATCH_IN_GROUP => ClientInputMsgGroupComMatch.fromJson(jsonValue)
        }
      case None => ??? // TODO: send bad message or something
    }
  }
}
