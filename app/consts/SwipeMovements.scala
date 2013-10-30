package consts

/**
 * This object represents an abstraction of the swipe movements we recognize from the device.
 * The device screen is split into 5 areas plus 4 invalid ones (see Areas)
 * This file lists the combinations of movements between adjacent areas.
 */
object SwipeMovements extends Enumeration {
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
