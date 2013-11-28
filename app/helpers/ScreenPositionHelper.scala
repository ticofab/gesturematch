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
      case 2 => {
        myMovement match {
          case INNERRIGHT | RIGHTINNER => PositionLeft
          case LEFTINNER | INNERLEFT => PositionRight
          case TOPINNER | INNERTOP => PositionBottom
          case BOTTOMINNER | INNERBOTTOM => PositionTop
          case _ => PositionUnknown
        }
      }
      case 4 => {
        myMovement match {
          // watch out: not including undetermined situations
          case LEFTBOTTOM | BOTTOMLEFT => PositionTopRight
          case RIGHTBOTTOM | BOTTOMRIGHT => PositionTopLeft
          case TOPLEFT | LEFTTOP => PositionBottomRight
          case TOPRIGHT | RIGHTTOP => PositionBottomLeft
          case _ => PositionUndetermined
        }
      }
      case _ => PositionUnknown
    }
  }

  /*
   * Four-device connection, identifies the first position based on the second movement.
   */
  def getCorrespondingStartPosition(secondMovement: SwipeMovement) = {
    secondMovement match {
      case LEFTTOP => PositionBottomLeft
      case LEFTBOTTOM => PositionTopLeft
      case RIGHTBOTTOM => PositionTopRight
      case RIGHTTOP => PositionBottomRight
      case BOTTOMLEFT => PositionBottomRight
      case BOTTOMRIGHT => PositionBottomLeft
      case TOPLEFT => PositionTopRight
      case TOPRIGHT => PositionTopLeft
      case _ => PositionUnknown
    }
  }

  /*
   * Four-device connection, gets the last position given the first movement & position.
   */
  def getCorrespondingFinalPosition(firstMovement: SwipeMovement, firstPosition: ScreenPosition) = {
    firstMovement match {
      case INNERRIGHT => {
        firstPosition match {
          case PositionBottomLeft => PositionTopLeft
          case PositionTopLeft => PositionBottomLeft
          case _ => PositionUnknown
        }
      }
      case INNERBOTTOM => {
        firstPosition match {
          case PositionTopLeft => PositionTopRight
          case PositionTopRight => PositionTopLeft
          case _ => PositionUnknown
        }
      }
      case INNERTOP => {
        firstPosition match {
          case PositionBottomLeft => PositionBottomRight
          case PositionBottomRight => PositionBottomLeft
          case _ => PositionUnknown
        }
      }
      case INNERLEFT => {
        firstPosition match {
          case PositionBottomRight => PositionTopRight
          case PositionTopRight => PositionBottomRight
          case _ => PositionUnknown
        }
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

