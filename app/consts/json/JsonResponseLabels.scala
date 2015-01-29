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

object JsonResponseLabels {
  // response kinds key
  val KIND_RESPONSE = "response"
  val KIND_INVALID_INPUT = "invalidInput"

  // -- reasons to match request
  val REASON_INVALID_REQUEST = "invalidRequest"
  val REASON_UNCERTAIN = "uncertain"
  val REASON_TIMEOUT = "timeout"
  val REASON_UNKNOWN_ERROR = "error"

  // -- reasons to leave group request
  val REASON_NOT_PART_OF_ANY_GROUP = "notPartOfAnyGroup"
  val REASON_NOT_PART_OF_THIS_GROUP = "notPartOfThisGroup"

  // -- reasons to delivery request
  val REASON_PAYLOAD_NOT_DELIVERED = "notDelivered"

  // matched messages labels
  val MYSELF_IN_GROUP = "myId"
  val OTHERS_IN_GROUP = "group"
  val GROUP_POSITION_SCHEME = "scheme"
}
