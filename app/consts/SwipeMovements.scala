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

package consts

/**
  * This object represents an abstraction of the swipe movements we recognize from the device.
  * The device screen is split into 5 areas plus 4 invalid ones (see Areas)
  * This file lists the combinations of movements between adjacent areas.
  */
object SwipeMovements extends Enumeration {
  type SwipeMovement = Value
  val UNKNOWN = Value("unknown")
  val INNERLEFT = Value(Areas.INNER.toString + Areas.LEFT.toString)
  val INNERBOTTOM = Value(Areas.INNER.toString + Areas.BOTTOM.toString)
  val INNERRIGHT = Value(Areas.INNER.toString + Areas.RIGHT.toString)
  val INNERTOP = Value(Areas.INNER.toString + Areas.TOP.toString)
  val LEFTINNER = Value(Areas.LEFT.toString + Areas.INNER.toString)
  val BOTTOMINNER = Value(Areas.BOTTOM.toString + Areas.INNER.toString)
  val RIGHTINNER = Value(Areas.RIGHT.toString + Areas.INNER.toString)
  val TOPINNER = Value(Areas.TOP.toString + Areas.INNER.toString)
  val LEFTBOTTOM = Value(Areas.LEFT.toString + Areas.BOTTOM.toString)
  val LEFTTOP = Value(Areas.LEFT.toString + Areas.TOP.toString)
  val RIGHTBOTTOM = Value(Areas.RIGHT.toString + Areas.BOTTOM.toString)
  val RIGHTTOP = Value(Areas.RIGHT.toString + Areas.TOP.toString)
  val BOTTOMLEFT = Value(Areas.BOTTOM.toString + Areas.LEFT.toString)
  val TOPLEFT = Value(Areas.TOP.toString + Areas.LEFT.toString)
  val BOTTOMRIGHT = Value(Areas.BOTTOM.toString + Areas.RIGHT.toString)
  val LEFTRIGHT = Value(Areas.LEFT.toString + Areas.RIGHT.toString)
  val RIGHTLEFT = Value(Areas.RIGHT.toString + Areas.LEFT.toString)
  val TOPBOTTOM = Value(Areas.TOP.toString + Areas.BOTTOM.toString)
  val BOTTOMTOP = Value(Areas.BOTTOM.toString + Areas.TOP.toString)
  val TOPRIGHT = Value(Areas.TOP.toString + Areas.RIGHT.toString)

  def getLegalNextOnes(movement: SwipeMovement): List[SwipeMovement] = {
    movement match {
      case INNERRIGHT | LEFTRIGHT | BOTTOMRIGHT | TOPRIGHT => List(LEFTRIGHT, LEFTINNER, LEFTBOTTOM, LEFTTOP)
      case INNERLEFT | RIGHTLEFT | BOTTOMLEFT | TOPLEFT => List(RIGHTLEFT, RIGHTINNER, RIGHTTOP, RIGHTBOTTOM)
      case LEFTBOTTOM | RIGHTBOTTOM | TOPBOTTOM | INNERBOTTOM => List(TOPLEFT, TOPINNER, TOPRIGHT, TOPBOTTOM)
      case LEFTTOP | RIGHTTOP | INNERTOP | BOTTOMTOP => List(BOTTOMLEFT, BOTTOMRIGHT, BOTTOMTOP, BOTTOMINNER)
      case LEFTINNER | RIGHTINNER | TOPINNER | BOTTOMINNER => List()
    }
  }

}


