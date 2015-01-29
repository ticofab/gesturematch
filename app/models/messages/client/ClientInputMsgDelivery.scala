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

import consts.json.{JsonGeneralLabels, JsonInputLabels}
import models.messages.client.base.ClientInputMsg
import play.api.libs.json.JsValue

case class ClientInputMsgDelivery(groupId: String,
                                  deliveryId: String,
                                  payload: String,
                                  tag: Option[String],
                                  recipients: Option[List[Int]],
                                  chunkNr: Option[Int],
                                  totalChunks: Option[Int]) extends ClientInputMsg

object ClientInputMsgDelivery {
  def fromJson(jsonValue: JsValue): ClientInputMsgDelivery = {
    // group stuff
    val groupId = (jsonValue \ JsonGeneralLabels.GROUP_ID).as[String]

    // delivery stuff
    val recipients: Option[List[Int]] = (jsonValue \ JsonInputLabels.INPUT_RECIPIENTS).asOpt[List[Int]]
    val payload: String = (jsonValue \ JsonGeneralLabels.PAYLOAD).as[String]
    val deliveryId: String = (jsonValue \ JsonInputLabels.INPUT_DELIVERY_ID).as[String]
    val tag: Option[String] = (jsonValue \ JsonInputLabels.INPUT_DELIVERY_TAG).asOpt[String]

    // partial delivery stuff
    val chunk: Option[Int] = (jsonValue \ JsonInputLabels.INPUT_CHUNK_NUMBER).asOpt[Int]
    val totalChunks: Option[Int] = (jsonValue \ JsonInputLabels.INPUT_TOTAL_CHUNKS).asOpt[Int]

    ClientInputMsgDelivery(groupId, deliveryId, payload, tag, recipients, chunk, totalChunks)
  }
}


