package helpers

import consts.SwipeMovements._
import models.PossibleMatching

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
  def swipesToMovement(swipeStart: Int, swipeEnd: Int): SwipeMovement = {
    val swipeValue: Int = swipeStart * 10 + swipeEnd
    swipeValue match {
      case 2 => TopLeft
      case 3 => TopRight
      case 4 => TopInner
      case 12 => BottomLeft
      case 13 => BottomRight
      case 14 => BottomInner
      case 20 => LeftTop
      case 21 => LeftBottom
      case 24 => LeftInner
      case 30 => RightTop
      case 31 => RightBottom
      case 34 => RightInner
      case 40 => InnerTop
      case 41 => InnerBottom
      case 42 => InnerLeft
      case 43 => InnerRight
      case _ => Unknown
    }
  }
}