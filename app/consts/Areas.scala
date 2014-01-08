package consts

import scala.util.Try

/**
 * The device screen is split into 5 areas plus 4 invalid ones:
 *
 *  ---------------------------------------------------
 *  |           |                         |           |
 *  |  Invalid  |          Top            |  Invalid  |
 *  |     5     |           0             |     5     |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |   Left    |         Inner           |   Right   |
 *  |    2      |           4             |     3     |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *  |           |                         |           |
 *  |  Invalid  |          Bottom         |  Invalid  |
 *  |     5     |            1            |     5     |
 *  |           |                         |           |
 *  | --------- | ----------------------- | --------- |
 *
 * These values need to be statically defined (as integers) as they are what comes through
 * the APIs when a client sends a matching request.
 */

object Areas extends Enumeration {
  type Areas = Value
  val TOP = Value("top")
  val BOTTOM = Value("bottom")
  val LEFT = Value("left")
  val RIGHT = Value("right")
  val INNER = Value("inner")
  val INVALID = Value("invalid")
  val OUTER = Value("outer")

  def getAreaFromString(area: String): Areas = Try(Areas.withName(area)) getOrElse INVALID

  def getValidOnes = this.values.filter(_ != INVALID)
}
