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

object JsonMessageLabels {
  // message kind key
  val KIND_MESSAGE = "message"

  // message type keys
  val MESSAGE_TYPE_DELIVERY = "delivery"
  val MESSAGE_TYPE_MATCHEE_LEFT_GROUP = "matcheeLeft"

  // message extra keys
  val MESSAGE_MATCHEE_ID = "matcheeId"


  // ideas:
  // - when a matchee leaves the group, don't destroy it but let the clients know how many
  //    matchees are left in the group
}
