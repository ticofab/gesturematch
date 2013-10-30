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
}