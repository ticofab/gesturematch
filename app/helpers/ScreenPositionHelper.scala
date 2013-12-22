package helpers

import consts.ScreenPositions._
import consts.SwipeMovements._
import consts.Areas._

/**
 * This object is a helper to understand device positions with respect to each other.
 */
object ScreenPositionHelper {

  /*
   * Understands a position given a movement. Attention: for the four-device case, it only works for positions
   * that are not undetermined. It basically doesn't work with all the starting and ending position.
   */
  def getPosition(myMovement: SwipeMovement, nrOfScreensInConnection: Int): ScreenPosition = {
    nrOfScreensInConnection match {
      case 2 =>
        myMovement match {
          case INNERRIGHT | RIGHTINNER => Left
          case LEFTINNER | INNERLEFT => Right
          case TOPINNER | INNERTOP => Bottom
          case BOTTOMINNER | INNERBOTTOM => Top
          case _ => Unknown
        }
      case 4 =>
        myMovement match {
          // watch out: not including undetermined situations
          case LEFTBOTTOM | BOTTOMLEFT => TopRight
          case RIGHTBOTTOM | BOTTOMRIGHT => TopLeft
          case TOPLEFT | LEFTTOP => BottomRight
          case TOPRIGHT | RIGHTTOP => BottomLeft
          case _ => Undetermined
        }
      case _ => Unknown
    }
  }

  /*
   * Four-device connection, identifies the first position based on the second movement.
   */
  def getCorrespondingStartPosition(secondMovement: SwipeMovement) = {
    secondMovement match {
      case LEFTTOP => BottomLeft
      case LEFTBOTTOM => TopLeft
      case RIGHTBOTTOM => TopRight
      case RIGHTTOP => BottomRight
      case BOTTOMLEFT => BottomRight
      case BOTTOMRIGHT => BottomLeft
      case TOPLEFT => TopRight
      case TOPRIGHT => TopLeft
      case _ => Unknown
    }
  }

  /*
   * Four-device connection, gets the last position given the first movement & position.
   */
  def getCorrespondingFinalPosition(firstMovement: SwipeMovement, firstPosition: ScreenPosition) = {
    firstMovement match {
      case INNERRIGHT =>
        firstPosition match {
          case BottomLeft => TopLeft
          case TopLeft => BottomLeft
          case _ => Unknown
        }
      case INNERBOTTOM =>
        firstPosition match {
          case TopLeft => TopRight
          case TopRight => TopLeft
          case _ => Unknown
        }
      case INNERTOP =>
        firstPosition match {
          case BottomLeft => BottomRight
          case BottomRight => BottomLeft
          case _ => Unknown
        }
      case INNERLEFT =>
        firstPosition match {
          case BottomRight => TopRight
          case TopRight => BottomRight
          case _ => Unknown
        }
    }
  }

  /*
   * Given one "arrival" position, it infers where the starting one must have been.
   */
  def getCorrespondingEntrance(exitArea: Areas) = {
    exitArea match {
      case BOTTOM => TOP
      case TOP => BOTTOM
      case LEFT => RIGHT
      case RIGHT => LEFT
      case _ => INVALID
    }
  }
}

