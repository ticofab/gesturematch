package helpers

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
      case InnerRight => makePossibleMatchingsListTwoAndFour(LeftInner, RightInner, LeftBottom, TopLeft, LeftTop, BottomLeft)
      case LeftInner => makePossibleMatchingsListTwoAndFour(InnerRight, InnerLeft, RightBottom, TopRight, RightTop, BottomRight)
      case RightInner => makePossibleMatchingsListTwoAndFour(InnerLeft, InnerRight, LeftBottom, TopLeft, LeftTop, BottomLeft)
      case InnerLeft => makePossibleMatchingsListTwoAndFour(RightInner, LeftInner, RightTop, BottomRight, RightBottom, TopRight)
      case InnerBottom => makePossibleMatchingsListTwoAndFour(TopInner, BottomInner, TopRight, LeftTop, TopLeft, RightTop)
      case TopInner => makePossibleMatchingsListTwoAndFour(InnerBottom, InnerTop, BottomLeft, RightBottom, BottomRight, LeftBottom)
      case BottomInner => makePossibleMatchingsListTwoAndFour(InnerTop, InnerBottom, TopRight, LeftTop, TopLeft, RightTop)
      case InnerTop => makePossibleMatchingsListTwoAndFour(BottomInner, TopInner, BottomRight, LeftBottom, BottomLeft, RightBottom)

      // groups are possible only with 4 devices
      case TopLeft => makePossibleMatchingsListFour(InnerRight, LeftBottom, RightInner, InnerBottom, RightTop, BottomInner)
      case LeftBottom => makePossibleMatchingsListFour(InnerRight, TopLeft, RightInner, InnerTop, BottomRight, TopInner)
      case BottomRight => makePossibleMatchingsListFour(InnerTop, LeftBottom, TopInner, InnerLeft, RightTop, LeftInner)
      case RightBottom => makePossibleMatchingsListFour(InnerLeft, TopRight, LeftInner, InnerTop, BottomLeft, TopInner)
      case TopRight => makePossibleMatchingsListFour(InnerLeft, RightBottom, LeftInner, InnerBottom, LeftTop, BottomInner)
      case LeftTop => makePossibleMatchingsListFour(InnerBottom, TopRight, BottomInner, InnerRight, BottomLeft, RightInner)
      case BottomLeft => makePossibleMatchingsListFour(InnerTop, RightBottom, TopInner, InnerRight, LeftTop, RightInner)
      case RightTop => makePossibleMatchingsListFour(InnerLeft, BottomRight, LeftInner, InnerBottom, TopLeft, BottomInner)

      case _ => List(new PossibleMatching(0, List()))
    }
  }

  /*
   * Translates a pair start- / end-swipe into a movement.
   */
  def swipesToMovement(swipeStart: Areas, swipeEnd: Areas): SwipeMovement = {
    val areasMix = swipeStart.toString + swipeEnd.toString
    Try(SwipeMovements.withName(areasMix)) getOrElse(Unknown)
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
    def getLongestComb(res: List[List[RequestToMatch]]) =
      res match {
        case Nil => Nil
        case x :: xs => res.reduceLeft((a, b) => if (a.length > b.length) a else b)
      }

    def getValidCombs(res: List[List[RequestToMatch]]) = {
      def validCombFilter(res: List[RequestToMatch]): Boolean = {
        val coolHead = isInnerX(res.head.movement)
        val coolEnd = isXInner(res.last.movement)
        coolHead && coolEnd
      }

      res.filter(validCombFilter(_))
    }

    def getCombinations(tileHistory: List[RequestToMatch], availableNewTiles: List[RequestToMatch]): List[(List[RequestToMatch], List[RequestToMatch])] = {

      def expand = availableNewTiles.filter(SwipeMovements.getLegalNextOnes(tileHistory.head.movement).contains(_)).map(elem => (elem :: tileHistory, availableNewTiles.diff(List(elem))))

      availableNewTiles match {
        case Nil => List()
        case x :: xs => {
          val expanded = expand

          def addChunks = {
            if (expanded == Nil) Nil
            else (for (xs <- expanded) yield getCombinations(xs._1, xs._2)).flatten
          }

          if (expanded == Nil) Nil
          else expanded ++ addChunks
        }
      }
    }

    def isInnerX(movement: SwipeMovement): Boolean = movement match {
      case InnerLeft | InnerRight | InnerTop | InnerBottom => true
      case _ => false
    }

    def isXInner(movement: SwipeMovement): Boolean = movement match {
      case InnerLeft | InnerRight | InnerTop | InnerBottom => true
      case _ => false
    }

    // find the one with Inner and use it as first
    val (head, tail) = matchingRequests.partition(r => isInnerX(r.movement))

    head match {

      // good, only one InnerX
      case x :: Nil => {
        // these intermediate values are here for clarity
        val skimmedResults = getCombinations(head, tail).map(x => x._1)
        val longestValidResult = getLongestComb(getValidCombs(skimmedResults)).reverse
        longestValidResult
      }

      // error, not a single InnerX request
      case Nil => List()

      // error, two or more InnerX requests
      case _ => List()

    }
  }
}
