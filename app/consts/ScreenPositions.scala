package consts

/**
 * This object provides a set of relative positions in a composition of devices.
 */
object ScreenPositions extends Enumeration {
  type ScreenPosition = Value

  // unknown position
  val Unknown = Value
  val Undetermined = Value
  val Left  = Value
  val Right = Value
  val Top = Value
  val Bottom = Value
  val TopLeft = Value
  val TopRight = Value
  val BottomLeft = Value
  val BottomRight = Value
}
