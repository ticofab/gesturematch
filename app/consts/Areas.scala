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


/**
 * The device screen is split into 5 areas plus 4 invalid ones:
 *
 * ---------------------------------------------------
 * |           |                         |           |
 * |  Invalid  |          Top            |  Invalid  |
 * |     5     |           0             |     5     |
 * |           |                         |           |
 * | --------- | ----------------------- | --------- |
 * |           |                         |           |
 * |           |                         |           |
 * |           |                         |           |
 * |           |                         |           |
 * |   Left    |         Inner           |   Right   |
 * |    2      |           4             |     3     |
 * |           |                         |           |
 * |           |                         |           |
 * |           |                         |           |
 * |           |                         |           |
 * | --------- | ----------------------- | --------- |
 * |           |                         |           |
 * |  Invalid  |          Bottom         |  Invalid  |
 * |     5     |            1            |     5     |
 * |           |                         |           |
 * | --------- | ----------------------- | --------- |
 *
 * These values need to be statically defined (as integers) as they are what comes through
 * the APIs when a client sends a matching request.
 */

object Areas extends Enumeration {

  case class Areas() extends Val {

    /** Imagine you want to put two devices next to each other. This operator checks that two areas can
      * be next to each other in a sequence of devices.
      *
      * @param other
      * another Areas object
      *
      * @return
      * true if the two areas can be next to each other in a sequence of devices
      */
    def =!=(other: Areas): Boolean = {
      this match {
        case TOP => other == BOTTOM
        case BOTTOM => other == TOP
        case RIGHT => other == LEFT
        case LEFT => other == RIGHT
        case _ => false
      }
    }
  }

  val INNER, TOP, RIGHT, BOTTOM, LEFT, INVALID, OUTER = Areas()

  def getAreaFromString(area: String): Areas =
    area match {
      case "top" => TOP
      case "bottom" => BOTTOM
      case "left" => LEFT
      case "right" => RIGHT
      case "inner" => INNER
      case "outer" => OUTER
      case _ => INVALID
    }

  def getValidOnes = this.values.filter(a => a != INVALID && a != OUTER)
}
