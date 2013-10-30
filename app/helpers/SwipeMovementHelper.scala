package helpers

import consts.SwipeMovements._

object SwipeMovementHelper {

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