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
          case InnerRight | RightInner => PositionLeft
          case LeftInner | InnerLeft => PositionRight
          case TopInner | InnerTop => PositionBottom
          case BottomInner | InnerBottom => PositionTop
          case _ => PositionUnknown
        }
      }
      case 4 => {
        myMovement match {
          // watch out: not including undetermined situations
          case LeftBottom | BottomLeft => PositionTopRight
          case RightBottom | BottomRight => PositionTopLeft
          case TopLeft | LeftTop => PositionBottomRight
          case TopRight | RightTop => PositionBottomLeft
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
      case LeftTop => PositionBottomLeft
      case LeftBottom => PositionTopLeft
      case RightBottom => PositionTopRight
      case RightTop => PositionBottomRight
      case BottomLeft => PositionBottomRight
      case BottomRight => PositionBottomLeft
      case TopLeft => PositionTopRight
      case TopRight => PositionTopLeft
      case _ => PositionUnknown
    }
  }

  /*
   * Four-device connection, gets the last position given the first movement & position.
   */
  def getCorrespondingFinalPosition(firstMovement: SwipeMovement, firstPosition: ScreenPosition) = {
    firstMovement match {
      case InnerRight => {
        firstPosition match {
          case PositionBottomLeft => PositionTopLeft
          case PositionTopLeft => PositionBottomLeft
          case _ => PositionUnknown
        }
      }
      case InnerBottom => {
        firstPosition match {
          case PositionTopLeft => PositionTopRight
          case PositionTopRight => PositionTopLeft
          case _ => PositionUnknown
        }
      }
      case InnerTop => {
        firstPosition match {
          case PositionBottomLeft => PositionBottomRight
          case PositionBottomRight => PositionBottomLeft
          case _ => PositionUnknown
        }
      }
      case InnerLeft => {
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
      case Bottom => Top
      case Top => Bottom
      case Left => Right
      case Right => Left
      case _ => Invalid
    }
  }
}

