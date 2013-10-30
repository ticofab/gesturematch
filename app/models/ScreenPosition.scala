package models

import models.SwipeMovement._

/**
 * This object provides an abstraction of device positions with respect to each other.
 */

// TODO: maybe this would be better being a List or some "more native" scala structure
object ScreenPosition extends Enumeration {
  type ScreenPosition = Value

  // unknown position
  val PositionUnknown, PositionUndetermined = Value

  // values for 2 devices
  val PositionLeft, PositionRight, PositionTop, PositionBottom = Value

  // values for 4 devices (not taking into account the device orientation) 
  val PositionTopLeft, PositionTopRight, PositionBottomLeft, PositionBottomRight = Value

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
}

