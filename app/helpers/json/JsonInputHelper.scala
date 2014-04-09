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

package helpers.json

import play.api.libs.json.{JsValue, Json}
import models.ClientInputMessages._
import play.api.Logger
import consts.json.{JsonGeneralLabels, JsonInputLabels}
import scala.Some

object JsonInputHelper {
  lazy val myName = JsonInputHelper.getClass.getSimpleName

  def parseInput(input: String): ClientInputMessage = {
    val jsonValue: JsValue = Json.parse(input)

    val inputType = (jsonValue \ JsonGeneralLabels.TYPE).asOpt[String]

    inputType match {
      case Some(header) =>
        Logger.info(s"$myName, input parsed, type $header.")

        header match {
          case JsonInputLabels.INPUT_TYPE_MATCH => ClientInputMessageMatch.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_LEAVE_GROUP => ClientInputMessageLeaveGroup.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_DISCONNECT => ClientInputMessageDisconnect.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_DELIVERY => ClientInputMessageDelivery.fromJson(jsonValue)
        }
      case None => ??? // TODO: send bad message or something
    }
  }
}
