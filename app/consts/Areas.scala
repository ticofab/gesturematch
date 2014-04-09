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

package consts

import scala.util.Try

/**
 * The device screen is split into 5 areas plus 4 invalid ones:
 *
 *  ---------------------------------------------------
 *  |           |                         |           |
 *  |  Invalid  |          Top            |  Invalid  |
 *  |     5     |           0             |     5     |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |   Left    |         Inner           |   Right   |
 *  |    2      |           4             |     3     |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *  |           |                         |           |
 *  |  Invalid  |          Bottom         |  Invalid  |
 *  |     5     |            1            |     5     |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *
 * These values need to be statically defined (as integers) as they are what comes through
 * the APIs when a client sends a matching request.
 */

object Areas extends Enumeration {
  type Areas = Value
  val TOP = Value("top")
  val BOTTOM = Value("bottom")
  val LEFT = Value("left")
  val RIGHT = Value("right")
  val INNER = Value("inner")
  val INVALID = Value("invalid")
  val OUTER = Value("outer")

  def getAreaFromString(area: String): Areas = Try(Areas.withName(area)) getOrElse INVALID

  def getValidOnes = this.values.filter(_ != INVALID)
}
