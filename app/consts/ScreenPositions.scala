package consts

/**
 * This object provides a set of relative positions in a composition of devices.
 */
object ScreenPositions extends Enumeration {
  type ScreenPosition = Value

  // unknown position
  val Unknown = Value("unknown")
  val Undetermined = Value("undetermined")
  val Start = Value("start")
  val Left  = Value("left")
  val Right = Value("right")
  val Top = Value("top")
  val Bottom = Value("bottom")
  val TopLeft = Value("topleft")
  val TopRight = Value("topright")
  val BottomLeft = Value("bottomleft")
  val BottomRight = Value("bottomright")
}
