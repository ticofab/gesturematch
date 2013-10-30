package consts

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

object Areas {
  val Top: Int = 0
  val Bottom: Int = 1
  val Left: Int = 2
  val Right: Int = 3
  val Inner: Int = 4
  val Invalid: Int = 5
}
