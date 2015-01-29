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

import models.matching.Delivery
import play.api.libs.json.{JsString, JsNumber, Json}
import consts.json.{JsonInputLabels, JsonGeneralLabels, JsonMessageLabels}

object JsonMessageHelper {

  def createMatcheeSendsPayloadMessage(groupId: String, senderId: Int, delivery: Delivery) = {
    var json = Json.obj(
      JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
      JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_DELIVERY,
      JsonGeneralLabels.GROUP_ID -> groupId,
      JsonMessageLabels.MESSAGE_MATCHEE_ID -> JsNumber(senderId),
      JsonInputLabels.INPUT_DELIVERY_ID -> delivery.id,
      JsonGeneralLabels.PAYLOAD -> delivery.payload
    )

    if (delivery.tag.isDefined) json = json + (JsonInputLabels.INPUT_DELIVERY_TAG, JsString(delivery.tag.get))
    if (delivery.chunk.isDefined) json = json + (JsonInputLabels.INPUT_CHUNK_NUMBER, JsNumber(delivery.chunk.get))
    if (delivery.totalChunks.isDefined) json = json + (JsonInputLabels.INPUT_TOTAL_CHUNKS, JsNumber(delivery.totalChunks.get))

    Json.stringify(json)
  }

  def createMatcheeLeftGroupMessage(groupId: String, leaverId: Int) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
        JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_MATCHEE_LEFT_GROUP,
        JsonGeneralLabels.GROUP_ID -> groupId,
        JsonMessageLabels.MESSAGE_MATCHEE_ID -> JsNumber(leaverId)
      )
    )
  }
}
