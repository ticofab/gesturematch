package helpers.movements

import consts.SwipeMovements._
import models.{RequestToMatch, PossibleMatching}
import consts.SwipeMovements
import consts.Areas.Areas
import scala.util.Try

object SwipeMovementHelper {
  private def makePossibleMatchingsListTwoAndFour(pos2: SwipeMovement,
                                                  pos4common: SwipeMovement,
                                                  pos4first1: SwipeMovement,
                                                  pos4first2: SwipeMovement,
                                                  pos4second1: SwipeMovement,
                                                  pos4second2: SwipeMovement) = {
    val pm1 = new PossibleMatching(2, List(pos2))
    val pm2 = new PossibleMatching(4, List(pos4common, pos4first1, pos4first2))
    val pm3 = new PossibleMatching(4, List(pos4common, pos4second1, pos4second2))
    List(pm1, pm2, pm3)
  }

  private def makePossibleMatchingsListFour(pos4first1: SwipeMovement,
                                            pos4first2: SwipeMovement,
                                            pos4first3: SwipeMovement,
                                            pos4second1: SwipeMovement,
                                            pos4second2: SwipeMovement,
                                            pos4second3: SwipeMovement) = {
    val pm1 = new PossibleMatching(4, List(pos4first1, pos4first2, pos4first3))
    val pm2 = new PossibleMatching(4, List(pos4second1, pos4second2, pos4second3))
    List(pm1, pm2)
  }

  /*
   * Given one movement, returns the possible combinations. Limited to
   *    - 2 or 4 devices
   *    - devices all aligned according to the vertical side, ie they all "point" in the same direction
   */
  def getPossibleMatching(movement: SwipeMovement): List[PossibleMatching] = {
    movement match {

      // groups are possible with 2 and 4 devices combination
      case INNERRIGHT => makePossibleMatchingsListTwoAndFour(LEFTINNER, RIGHTINNER, LEFTBOTTOM, TOPLEFT, LEFTTOP, BOTTOMLEFT)
      case LEFTINNER => makePossibleMatchingsListTwoAndFour(INNERRIGHT, INNERLEFT, RIGHTBOTTOM, TOPRIGHT, RIGHTTOP, BOTTOMRIGHT)
      case RIGHTINNER => makePossibleMatchingsListTwoAndFour(INNERLEFT, INNERRIGHT, LEFTBOTTOM, TOPLEFT, LEFTTOP, BOTTOMLEFT)
      case INNERLEFT => makePossibleMatchingsListTwoAndFour(RIGHTINNER, LEFTINNER, RIGHTTOP, BOTTOMRIGHT, RIGHTBOTTOM, TOPRIGHT)
      case INNERBOTTOM => makePossibleMatchingsListTwoAndFour(TOPINNER, BOTTOMINNER, TOPRIGHT, LEFTTOP, TOPLEFT, RIGHTTOP)
      case TOPINNER => makePossibleMatchingsListTwoAndFour(INNERBOTTOM, INNERTOP, BOTTOMLEFT, RIGHTBOTTOM, BOTTOMRIGHT, LEFTBOTTOM)
      case BOTTOMINNER => makePossibleMatchingsListTwoAndFour(INNERTOP, INNERBOTTOM, TOPRIGHT, LEFTTOP, TOPLEFT, RIGHTTOP)
      case INNERTOP => makePossibleMatchingsListTwoAndFour(BOTTOMINNER, TOPINNER, BOTTOMRIGHT, LEFTBOTTOM, BOTTOMLEFT, RIGHTBOTTOM)

      // groups are possible only with 4 devices
      case TOPLEFT => makePossibleMatchingsListFour(INNERRIGHT, LEFTBOTTOM, RIGHTINNER, INNERBOTTOM, RIGHTTOP, BOTTOMINNER)
      case LEFTBOTTOM => makePossibleMatchingsListFour(INNERRIGHT, TOPLEFT, RIGHTINNER, INNERTOP, BOTTOMRIGHT, TOPINNER)
      case BOTTOMRIGHT => makePossibleMatchingsListFour(INNERTOP, LEFTBOTTOM, TOPINNER, INNERLEFT, RIGHTTOP, LEFTINNER)
      case RIGHTBOTTOM => makePossibleMatchingsListFour(INNERLEFT, TOPRIGHT, LEFTINNER, INNERTOP, BOTTOMLEFT, TOPINNER)
      case TOPRIGHT => makePossibleMatchingsListFour(INNERLEFT, RIGHTBOTTOM, LEFTINNER, INNERBOTTOM, LEFTTOP, BOTTOMINNER)
      case LEFTTOP => makePossibleMatchingsListFour(INNERBOTTOM, TOPRIGHT, BOTTOMINNER, INNERRIGHT, BOTTOMLEFT, RIGHTINNER)
      case BOTTOMLEFT => makePossibleMatchingsListFour(INNERTOP, RIGHTBOTTOM, TOPINNER, INNERRIGHT, LEFTTOP, RIGHTINNER)
      case RIGHTTOP => makePossibleMatchingsListFour(INNERLEFT, BOTTOMRIGHT, LEFTINNER, INNERBOTTOM, TOPLEFT, BOTTOMINNER)

      case _ => List(new PossibleMatching(0, List()))
    }
  }

  /*
   * Translates a pair start- / end-swipe into a movement.
   */
  def swipesToMovement(swipeStart: Areas, swipeEnd: Areas): SwipeMovement = {
    val areasMix = swipeStart.toString + swipeEnd.toString
    Try(SwipeMovements.withName(areasMix)) getOrElse UNKNOWN
  }

  /**
   * Given a list of requests, it identifies the longest pattern that matches them all.
   *
   * @param matchingRequests
   * All the requests that we've been able to match based on other criteria.
   *
   * @return
   * The longest closed sequence possible given the starting point and the other requests.
   */
  def getMatchedPattern(matchingRequests: List[RequestToMatch]): List[RequestToMatch] = {
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
        // debug
        val filtered = availableNewTiles.filter(r => SwipeMovements.getLegalNextOnes(tileHistory.head.movement).contains(r.movement))
        val mapped =  filtered.map(elem => (elem :: tileHistory, availableNewTiles.diff(List(elem))))
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
        val longestValidResult = getLongestComb(valid)
        val reversed = longestValidResult.reverse
        reversed

      // error, not a single InnerX request
      case Nil => List()

      // error, two or more InnerX requests
      case _ => List()

    }
  }
}
