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

package helpers.movements

import consts.SwipeMovements._
import models.{RequestToMatch, PossibleMatching}
import consts.SwipeMovements
import consts.Areas.Areas
import scala.util.Try

object SwipeMovementHelper {

  /*
   * Translates a pair start- / end-swipe into a movement.
   */
  def swipesToMovement(swipeStart: Areas, swipeEnd: Areas): SwipeMovement = {
    val areasMix = swipeStart.toString + swipeEnd.toString
    Try(SwipeMovements.withName(areasMix)) getOrElse UNKNOWN
  }
}
