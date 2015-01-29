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

package models.scheme

import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
import play.api.libs.json.{JsObject, Json, __}

case class DeviceInScheme(id: Int, x: Int, y: Int)

object DeviceInScheme {
  val ID = "id"
  val X = "x"
  val Y = "y"

  def toJson(deviceInScheme: DeviceInScheme): JsObject = Json.obj(
    ID -> deviceInScheme.id,
    X -> deviceInScheme.x,
    Y -> deviceInScheme.y
  )

  implicit val deviceInSchemeWrites = (
    (__ \ ID).write[Int] and
      (__ \ X).write[Int] and
      (__ \ Y).write[Int]
    )(unlift(DeviceInScheme.unapply))
}

