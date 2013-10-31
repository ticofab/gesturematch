package helpers

import consts.SwipeMovements._

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


  def areMovementsCompatible(mov1: SwipeMovement, mov2: SwipeMovement, ndevices: Int): Boolean = {

    if (ndevices == 2) {
      val pair: (SwipeMovement, SwipeMovement) = (mov1, mov2)
      pair match {
        case (LeftInner, InnerRight) => true
        case (InnerRight, LeftInner) => true
        case (RightInner, InnerLeft) => true
        case (InnerLeft, RightInner) => true
        case (InnerBottom, TopInner) => true
        case (TopInner, InnerBottom) => true
        case (BottomInner, InnerTop) => true
        case (InnerTop, BottomInner) => true
        case _ => false
      }
    } else if (ndevices == 4) {
      mov1 match {
        case LeftInner => mov2 match {
          case RightBottom | TopRight | BottomRight | RightTop | InnerLeft => true
          case _ => false
        }
        case InnerRight => mov2 match {
          case LeftBottom | TopLeft | LeftTop | BottomLeft | RightInner => true
          case _ => false
        }
        case RightInner => mov2 match {
          case LeftBottom | TopLeft | BottomLeft | LeftTop | InnerRight => true
          case _ => false
        }
        case InnerLeft => mov2 match {
          case RightBottom | TopRight | RightTop | BottomRight | LeftInner => true
          case _ => false
        }
        case InnerBottom => mov2 match {
          case TopRight | LeftTop | TopLeft | RightTop | BottomInner => true
          case _ => false
        }
        case TopInner => mov2 match {
          case LeftBottom | BottomRight | RightBottom | BottomLeft | InnerTop => true
          case _ => false
        }
        case BottomInner => mov2 match {
          case RightTop | TopLeft | LeftTop | TopRight | InnerBottom => true
          case _ => false
        }
        case InnerTop => mov2 match {
          case BottomRight | LeftBottom | BottomLeft | RightBottom | TopInner => true
          case _ => false
        }
        case TopLeft => mov2 match {
          case InnerRight | LeftBottom | RightInner | InnerBottom | RightTop | BottomInner => true
          case _ => false
        }
        case LeftBottom => mov2 match {
          case InnerRight | TopLeft | RightInner | BottomRight | InnerTop | TopInner => true
          case _ => false
        }
        case BottomRight => mov2 match {
          case InnerTop | LeftInner | TopInner | RightTop | LeftInner | InnerLeft => true
          case _ => false
        }
        case RightBottom => mov2 match {
          case InnerLeft | TopRight | LeftInner | TopInner | InnerTop | BottomLeft => true
          case _ => false
        }
        case TopRight => mov2 match {
          case InnerLeft | RightBottom | LeftInner | InnerBottom | LeftTop | BottomInner => true
          case _ => false
        }
        case LeftTop => mov2 match {
          case InnerBottom | BottomInner | TopRight | InnerRight | BottomLeft | RightInner => true
          case _ => false
        }
        case BottomLeft => mov2 match {
          case InnerTop | RightBottom | TopInner | RightInner | InnerRight | LeftTop => true
          case _ => false
        }
        case RightTop => mov2 match {
          case InnerLeft | LeftInner | BottomRight | TopLeft | InnerBottom | BottomInner => true
          case _ => false
        }
        case _ => false
      }
    } else if (ndevices == 6) {
      // not supported yet
      false
    } else {
      // not supported
      false
    }
  }

}
