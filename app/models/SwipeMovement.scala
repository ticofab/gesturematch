package models

/**
 * This object represents an abstraction of the swipe movements we recognize from the device.
 * The device screen is split into 5 areas plus 4 invalid ones:
 *
 *  ---------------------------------------------------
 *  |           |                         |           |
 *  |  Invalid  |          Top            |  Invalid  |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |   Left    |         Inner           |   Right   |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *  |           |                         |           |
 *  |  Invalid  |          Bottom         |  Invalid  |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *
 *  and these below are the combinations of movements between adjacent areas.
 */

// TODO: maybe this would be better being a list or some "nativer" scala structure
object SwipeMovement extends Enumeration {
  type SwipeMovement = Value
  val Unknown,
  InnerLeft,
  InnerBottom,
  InnerRight,
  InnerTop,
  LeftInner,
  BottomInner,
  RightInner,
  TopInner,
  LeftBottom,
  LeftTop,
  RightBottom,
  RightTop,
  BottomLeft,
  TopLeft,
  BottomRight,
  TopRight = Value

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