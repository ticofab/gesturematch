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

package consts

/**
 * This object provides a set of relative positions in a composition of devices.
 */
object ScreenPositions extends Enumeration {
  type ScreenPosition = Value

  // unknown position
  val Unknown = Value("unknown")
  val Undetermined = Value("undetermined")
  val Start = Value("start")
  val Left  = Value("left")
  val Right = Value("right")
  val Top = Value("top")
  val Bottom = Value("bottom")
  val TopLeft = Value("topleft")
  val TopRight = Value("topright")
  val BottomLeft = Value("bottomleft")
  val BottomRight = Value("bottomright")
}
