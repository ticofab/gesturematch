package consts

/**
 * This object provides a set of relative positions in a composition of devices.
 */
object ScreenPositions extends Enumeration {
  type ScreenPosition = Value

  // unknown position
  val PositionUnknown, PositionUndetermined = Value

  // values for composition of 2 devices
  val PositionLeft, PositionRight, PositionTop, PositionBottom = Value

  // values for composition of 4 devices (not taking into account the device orientation)
  val PositionTopLeft, PositionTopRight, PositionBottomLeft, PositionBottomRight = Value
}
