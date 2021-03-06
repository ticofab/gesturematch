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

package helpers.matchers.swipe

import consts.Areas._
import consts.ScreenPositions.ScreenPosition
import consts.{Areas, ScreenPositions}
import models.matching.base.MatchRequest
import play.api.Logger

object SwipeMatchingHelper {
  /**
    * Given a list of requests, it identifies the longest pattern that matches them all.
    *
    * @param matchingRequests
    * All the requests that we've been able to match based on other criteria.
    * @return
    * A pair (List[MatchRequest], Boolean) where the first is the longest closed sequence possible
    * given all the requests, and the second indicates whether it is unique or not.
    */
  def getMatchedPattern(matchingRequests: List[MatchRequest]): (List[MatchRequest], Boolean) = {

    type Path = List[MatchRequest]

    implicit class PathWrapper(path: Path) {
      def isPrepandable(request: MatchRequest) = {
        if (path.isEmpty) request.areaEnd == INNER
        else path.head.areaStart isChainable request.areaEnd
      }
    }

    def findPatterns(source: List[MatchRequest], path: Path): List[Path] = {
      val flatmapped = source.zipWithIndex.filter(ti => path.isPrepandable(ti._1)).flatMap(ti => {
        var res = findPatterns(source.take(ti._2) ++ source.drop(ti._2 + 1), ti._1 +: path)
        if (ti._1.areaStart == INNER) res = res :+ (ti._1 +: path)
        res
      })
      flatmapped
    }

    val paths = findPatterns(matchingRequests, List())
    if (paths.isEmpty) {
      Logger.debug("Not a single path found.")
      (Nil, false)
    } else {
      val longestPaths: (Int, List[Path]) = paths.groupBy(_.length).maxBy(_._1)
      Logger.debug(s"Found ${longestPaths._2.length} paths of length ${longestPaths._1}")
      (longestPaths._2.head, longestPaths._2.length == 1)
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
