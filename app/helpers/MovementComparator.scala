package helpers

import consts.SwipeMovements._

/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 30/10/13
 * Time: 11:20
 */
object MovementComparator {
  /*
  * This method takes two swipe movements, coming from two different devices, and understands how many devices are
  * in a connection. It only works with n == 2 and n == 4.
  */
  def compareMovement(mov1: SwipeMovement, mov2: SwipeMovement): Int = {
    mov1 match {
      case LeftInner => {
        mov2 match {
          case InnerRight => 2
          case RightBottom | TopRight | BottomRight | RightTop | InnerLeft => 4
          case _ => 0
        }
      }
      case InnerRight => {
        mov2 match {
          case LeftInner => 2
          case LeftBottom | TopLeft | LeftTop | BottomLeft | RightInner => 4
          case _ => 0
        }
      }
      case RightInner => {
        mov2 match {
          case InnerLeft => 2
          case LeftBottom | TopLeft | BottomLeft | LeftTop | InnerRight => 4
          case _ => 0
        }
      }
      case InnerLeft => {
        mov2 match {
          case RightInner => 2
          case RightBottom | TopRight | RightTop | BottomRight | LeftInner => 4
          case _ => 0
        }
      }
      case InnerBottom => {
        mov2 match {
          case TopInner => 2
          case TopRight | LeftTop | TopLeft | RightTop | BottomInner => 4
          case _ => 0
        }
      }
      case TopInner => {
        mov2 match {
          case InnerBottom => 2
          case LeftBottom | BottomRight | RightBottom | BottomLeft | InnerTop => 4
          case _ => 0
        }
      }
      case BottomInner => {
        mov2 match {
          case InnerTop => 2
          case RightTop | TopLeft | LeftTop | TopRight | InnerBottom => 4
          case _ => 0
        }
      }
      case InnerTop => {
        mov2 match {
          case BottomInner => 2
          case BottomRight | LeftBottom | BottomLeft | RightBottom | TopInner => 4
          case _ => 0
        }
      }
      case TopLeft => {
        mov2 match {
          case InnerRight | LeftBottom | RightInner | InnerBottom | RightTop | BottomInner => 4
          case _ => 0
        }
      }
      case LeftBottom => {
        mov2 match {
          case InnerRight | TopLeft | RightInner | BottomRight | InnerTop | TopInner => 4
          case _ => 0
        }
      }
      case BottomRight => {
        mov2 match {
          case InnerTop | LeftInner | TopInner | RightTop | LeftInner | InnerLeft => 4
          case _ => 0
        }
      }
      case RightBottom => {
        mov2 match {
          case InnerLeft | TopRight | LeftInner | TopInner | InnerTop | BottomLeft => 4
          case _ => 0
        }
      }
      case TopRight => {
        mov2 match {
          case InnerLeft | RightBottom | LeftInner | InnerBottom | LeftTop | BottomInner => 4
          case _ => 0
        }
      }
      case LeftTop => {
        mov2 match {
          case InnerBottom | BottomInner | TopRight | InnerRight | BottomLeft | RightInner => 4
          case _ => 0
        }
      }
      case BottomLeft => {
        mov2 match {
          case InnerTop | RightBottom | TopInner | RightInner | InnerRight | LeftTop => 4
          case _ => 0
        }
      }
      case RightTop => {
        mov2 match {
          case InnerLeft | LeftInner | BottomRight | TopLeft | InnerBottom | BottomInner => 4
          case _ => 0
        }
      }
      case _ => 0
    }
  }
}
