package helpers.presence

import models.RequestToMatch
import consts.SwipeMovements
import consts.SwipeMovements._

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
    def getLongestComb(res: List[List[RequestToMatch]]): List[RequestToMatch] =
      res match {
        case Nil => Nil
        case x :: xs => res.reduceLeft((a, b) => if (a.length > b.length) a else b)
      }

    def getValidCombs(res: List[List[RequestToMatch]]) = {
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

      // good, only one InnerX
      case x :: Nil =>
        // these intermediate values are here for clarity
        val combs = getCombinations(head, tail)
        val skimmedResults = combs.map(x => x._1)
        val valid = getValidCombs(skimmedResults)
        if (valid.length == 1) {
          (valid.head.reverse, true)
        } else {
          val longestValidResult = getLongestComb(valid)
          (longestValidResult.reverse, false)
        }


      // error, not a single InnerX request
      case Nil => (Nil, false)

      // error, two or more InnerX requests
      case _ => (Nil, false)

    }
  }
}
