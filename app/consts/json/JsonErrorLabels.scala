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

package consts.json

object JsonErrorLabels {
  // kind
  val KIND_ERROR = "error"

  // types
  val TYPE_SERVER_ERROR = "server_error"
  val TYPE_BAD_INPUT_ERROR = "bad_json_error"

  // reasons
  val REASON_SERVER_ERROR_NO_ACTOR = "the connection cannot be managed"
}
