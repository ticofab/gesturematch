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
  LeftRight,
  RightLeft,
  TopBottom,
  BottomTop,
  TopRight = Value

  def getLegalNextOnes(movement: SwipeMovement): List[SwipeMovement] = {
    movement match {
      case InnerRight | LeftRight | BottomRight | TopRight => List(LeftRight, LeftInner, LeftBottom, LeftTop)
      case InnerLeft | RightLeft | BottomLeft | TopLeft => List(RightLeft, RightInner, RightTop, RightBottom)
      case LeftBottom | RightBottom | TopBottom | InnerBottom => List(TopLeft, TopInner, TopRight, TopBottom)
      case LeftTop | RightTop | InnerTop | BottomTop => List(BottomLeft, BottomRight, BottomTop, BottomInner)
      case LeftInner | RightInner | TopInner | BottomInner => List()
    }
  }

}


