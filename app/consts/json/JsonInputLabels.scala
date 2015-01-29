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

package consts.json

object JsonInputLabels {
  // ----------------------------------------------
  // input type key
  // ----------------------------------------------
  val TYPE_INPUT = "input"

  // ----------------------------------------------
  // input type keys
  // ----------------------------------------------
  val INPUT_TYPE_MATCH_CREATE = "match"
  val INPUT_TYPE_MATCH_IN_GROUP = "matchInGroup"
  val INPUT_TYPE_LEAVE_GROUP = "leaveGroup"
  val INPUT_TYPE_DISCONNECT = "disconnect"
  val INPUT_TYPE_DELIVERY = "delivery"

  // ----------------------------------------------
  // input match keyy
  // ----------------------------------------------
  val MATCH_INPUT_CRITERIA = "criteria"
  val MATCH_INPUT_LATITUDE = "latitude"
  val MATCH_INPUT_LONGITUDE = "longitude"
  val MATCH_INPUT_AREASTART = "areaStart"
  val MATCH_INPUT_AREAEND = "areaEnd"
  val MATCH_INPUT_DEVICEID = "deviceId"
  val MATCH_INPUT_EQUALITYPARAM = "equalityParam"
  val MATCH_INPUT_GROUPID = "groupId"

  // ----------------------------------------------
  // input delivery keys
  // ----------------------------------------------
  val INPUT_RECIPIENTS = "recipients"
  val INPUT_CHUNK_NUMBER = "chunkNr"
  val INPUT_TOTAL_CHUNKS = "totalChunks"
  val INPUT_DELIVERY_ID = "deliveryId"
  val INPUT_DELIVERY_TAG = "tag"
}
