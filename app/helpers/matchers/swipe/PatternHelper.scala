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

package helpers.matchers.swipe

import models.RequestToMatch
import consts.{Areas, ScreenPositions, SwipeMovements}
import consts.SwipeMovements._
import consts.ScreenPositions.ScreenPosition
import consts.Areas.Areas
import consts.Areas.Areas
import play.api.Logger

object PatternHelper {
  /**
   * Given a list of requests, it identifies the longest pattern that matches them all.
   *
   * @param matchingRequests
   * All the requests that we've been able to match based on other criteria.
   *
   * @return
   * A pair (List[RequestToMatch], Boolean) where the first is the longest closed sequence possible
   * given all the requests, and the second indicates whether it is unique or not.
   */
  def getMatchedPattern(matchingRequests: List[RequestToMatch]): (List[RequestToMatch], Boolean) = {

    def getLongestCombinations(res: List[List[RequestToMatch]]): List[List[RequestToMatch]] =
      res match {
        case Nil => Nil
        case x :: xs =>
          val longestCombLength = res.maxBy(_.length).length
          Logger.debug(s"the longest combination is $longestCombLength long.")
          res.filter(_.length == longestCombLength)
      }

    def getValidCombinations(res: List[List[RequestToMatch]]) = {
      def validCombFilter(res: List[RequestToMatch]): Boolean = {
        // note! it assumes that the collection starts with the last request,
        //   which is then the XInner, and finishes with the first (InnerX)
        val coolHead = isXInner(res.head.movement)
        val coolEnd = isInnerX(res.last.movement)
        coolHead && coolEnd
      }

      res.filter(validCombFilter)
    }

    def getCombinations(tileHistory: List[RequestToMatch], availableNewTiles: List[RequestToMatch]): List[(List[RequestToMatch], List[RequestToMatch])] = {

      def expand = {
        val filtered = availableNewTiles.filter(r => SwipeMovements.getLegalNextOnes(tileHistory.head.movement).contains(r.movement))
        val mapped = filtered.map(elem => (elem :: tileHistory, availableNewTiles.diff(List(elem))))
        mapped
      }

      availableNewTiles match {
        case Nil => List()
        case x :: xs =>
          val expanded = expand

          def addChunks() = {
            if (expanded == Nil) Nil
            else (for (xs <- expanded) yield getCombinations(xs._1, xs._2)).flatten
          }

          if (expanded == Nil) Nil
          else expanded ++ addChunks
      }
    }

    def isInnerX(movement: SwipeMovement): Boolean = movement match {
      case INNERLEFT | INNERRIGHT | INNERTOP | INNERBOTTOM => true
      case _ => false
    }

    def isXInner(movement: SwipeMovement): Boolean = movement match {
      case LEFTINNER | RIGHTINNER | TOPINNER | BOTTOMINNER => true
      case _ => false
    }

    // find the one with Inner and use it as first
    val (head, tail) = matchingRequests.partition(r => isInnerX(r.movement))

    head match {

      // error, not a single InnerX request
      case Nil => (Nil, false)

      // good, only one InnerX
      case x :: Nil =>
        // these intermediate values are here for clarity
        val combs = getCombinations(head, tail)
        val skimmedResults = combs.map(x => x._1)
        val validCombinations = getValidCombinations(skimmedResults)

        if (validCombinations.isEmpty) {
          (Nil, false)
        } else {
          // return the first longest combination
          Logger.debug(s"there are ${validCombinations.length} valid combinations.")
          val longestCombinations = getLongestCombinations(validCombinations)
          Logger.debug(s"there are ${longestCombinations.length} longest combinations.")
          (longestCombinations.head.reverse, longestCombinations.length == 1)
        }

      // error, two or more InnerX requests
      case _ => (Nil, false)

    }
  }

  def getDeviceSchemePosition(swipeStart: Areas): ScreenPosition = {
    swipeStart match {
      case Areas.INNER => ScreenPositions.Start
      case Areas.TOP => ScreenPositions.Bottom
      case Areas.LEFT => ScreenPositions.Right
      case Areas.RIGHT => ScreenPositions.Left
      case Areas.BOTTOM => ScreenPositions.Top
      case _ => ScreenPositions.Unknown

    }
  }
}
