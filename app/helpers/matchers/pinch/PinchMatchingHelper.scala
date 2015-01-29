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

package helpers.matchers.pinch

import consts.ScreenPositions
import consts.ScreenPositions.ScreenPosition
import consts.SwipeMovements._
import models.matching.base.MatchRequest

object PinchMatchingHelper {

  /*
   * Given one movement, returns the possible combinations. Limited to
   *    - 2 or 4 devices
   *    - devices all aligned according to the vertical side, ie they all "point" in the same direction
   */
  def getMatchingMovement(movement: SwipeMovement): SwipeMovement = {
    movement match {

      // groups are possible with 2 and 4 devices combination
      case INNERRIGHT => INNERLEFT
      case INNERLEFT => INNERRIGHT
      case INNERTOP => INNERBOTTOM
      case INNERBOTTOM => INNERTOP

      // TODO: throw exception
      case _ => UNKNOWN
    }
  }

  def getPosition(movement: SwipeMovement): ScreenPosition = {
    movement match {
      case INNERRIGHT => ScreenPositions.Left
      case INNERTOP => ScreenPositions.Bottom
      case INNERLEFT => ScreenPositions.Right
      case INNERBOTTOM => ScreenPositions.Top

      // TODO: throw exception?
      case _ => ScreenPositions.Undetermined
    }
  }

  def getMatchingGroup(request: MatchRequest, existingRequests: List[MatchRequest]): List[MatchRequest] = {

    // get the possible matching movement for the given request
    val matchingMovement: SwipeMovement = PinchMatchingHelper.getMatchingMovement(request.movement)

    // get all the existing requests that could be part of one of the possible groups
    val matches: List[MatchRequest] = for {
      prevReq <- existingRequests // for all the previous requests, considered if
      if prevReq.movement == matchingMovement // its movement is necessary
    } yield prevReq

    matches
  }
}
