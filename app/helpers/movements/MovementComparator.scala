package helpers.movements

import consts.SwipeMovements._

object MovementComparator {
  /*
  * This method takes two swipe movements, coming from two different devices, and understands how many devices are
  * in a connection. It only works with n == 2 and n == 4.
  */
  def compareMovement(mov1: SwipeMovement, mov2: SwipeMovement): Int = {
    mov1 match {
      case LEFTINNER => {
        mov2 match {
          case INNERRIGHT => 2
          case RIGHTBOTTOM | TOPRIGHT | BOTTOMRIGHT | RIGHTTOP | INNERLEFT => 4
          case _ => 0
        }
      }
      case INNERRIGHT => {
        mov2 match {
          case LEFTINNER => 2
          case LEFTBOTTOM | TOPLEFT | LEFTTOP | BOTTOMLEFT | RIGHTINNER => 4
          case _ => 0
        }
      }
      case RIGHTINNER => {
        mov2 match {
          case INNERLEFT => 2
          case LEFTBOTTOM | TOPLEFT | BOTTOMLEFT | LEFTTOP | INNERRIGHT => 4
          case _ => 0
        }
      }
      case INNERLEFT => {
        mov2 match {
          case RIGHTINNER => 2
          case RIGHTBOTTOM | TOPRIGHT | RIGHTTOP | BOTTOMRIGHT | LEFTINNER => 4
          case _ => 0
        }
      }
      case INNERBOTTOM => {
        mov2 match {
          case TOPINNER => 2
          case TOPRIGHT | LEFTTOP | TOPLEFT | RIGHTTOP | BOTTOMINNER => 4
          case _ => 0
        }
      }
      case TOPINNER => {
        mov2 match {
          case INNERBOTTOM => 2
          case LEFTBOTTOM | BOTTOMRIGHT | RIGHTBOTTOM | BOTTOMLEFT | INNERTOP => 4
          case _ => 0
        }
      }
      case BOTTOMINNER => {
        mov2 match {
          case INNERTOP => 2
          case RIGHTTOP | TOPLEFT | LEFTTOP | TOPRIGHT | INNERBOTTOM => 4
          case _ => 0
        }
      }
      case INNERTOP => {
        mov2 match {
          case BOTTOMINNER => 2
          case BOTTOMRIGHT | LEFTBOTTOM | BOTTOMLEFT | RIGHTBOTTOM | TOPINNER => 4
          case _ => 0
        }
      }
      case TOPLEFT => {
        mov2 match {
          case INNERRIGHT | LEFTBOTTOM | RIGHTINNER | INNERBOTTOM | RIGHTTOP | BOTTOMINNER => 4
          case _ => 0
        }
      }
      case LEFTBOTTOM => {
        mov2 match {
          case INNERRIGHT | TOPLEFT | RIGHTINNER | BOTTOMRIGHT | INNERTOP | TOPINNER => 4
          case _ => 0
        }
      }
      case BOTTOMRIGHT => {
        mov2 match {
          case INNERTOP | LEFTINNER | TOPINNER | RIGHTTOP | LEFTINNER | INNERLEFT => 4
          case _ => 0
        }
      }
      case RIGHTBOTTOM => {
        mov2 match {
          case INNERLEFT | TOPRIGHT | LEFTINNER | TOPINNER | INNERTOP | BOTTOMLEFT => 4
          case _ => 0
        }
      }
      case TOPRIGHT => {
        mov2 match {
          case INNERLEFT | RIGHTBOTTOM | LEFTINNER | INNERBOTTOM | LEFTTOP | BOTTOMINNER => 4
          case _ => 0
        }
      }
      case LEFTTOP => {
        mov2 match {
          case INNERBOTTOM | BOTTOMINNER | TOPRIGHT | INNERRIGHT | BOTTOMLEFT | RIGHTINNER => 4
          case _ => 0
        }
      }
      case BOTTOMLEFT => {
        mov2 match {
          case INNERTOP | RIGHTBOTTOM | TOPINNER | RIGHTINNER | INNERRIGHT | LEFTTOP => 4
          case _ => 0
        }
      }
      case RIGHTTOP => {
        mov2 match {
          case INNERLEFT | LEFTINNER | BOTTOMRIGHT | TOPLEFT | INNERBOTTOM | BOTTOMINNER => 4
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
        case (LEFTINNER, INNERRIGHT) => true
        case (INNERRIGHT, LEFTINNER) => true
        case (RIGHTINNER, INNERLEFT) => true
        case (INNERLEFT, RIGHTINNER) => true
        case (INNERBOTTOM, TOPINNER) => true
        case (TOPINNER, INNERBOTTOM) => true
        case (BOTTOMINNER, INNERTOP) => true
        case (INNERTOP, BOTTOMINNER) => true
        case _ => false
      }
    } else if (ndevices == 4) {
      mov1 match {
        case LEFTINNER => mov2 match {
          case RIGHTBOTTOM | TOPRIGHT | BOTTOMRIGHT | RIGHTTOP | INNERLEFT => true
          case _ => false
        }
        case INNERRIGHT => mov2 match {
          case LEFTBOTTOM | TOPLEFT | LEFTTOP | BOTTOMLEFT | RIGHTINNER => true
          case _ => false
        }
        case RIGHTINNER => mov2 match {
          case LEFTBOTTOM | TOPLEFT | BOTTOMLEFT | LEFTTOP | INNERRIGHT => true
          case _ => false
        }
        case INNERLEFT => mov2 match {
          case RIGHTBOTTOM | TOPRIGHT | RIGHTTOP | BOTTOMRIGHT | LEFTINNER => true
          case _ => false
        }
        case INNERBOTTOM => mov2 match {
          case TOPRIGHT | LEFTTOP | TOPLEFT | RIGHTTOP | BOTTOMINNER => true
          case _ => false
        }
        case TOPINNER => mov2 match {
          case LEFTBOTTOM | BOTTOMRIGHT | RIGHTBOTTOM | BOTTOMLEFT | INNERTOP => true
          case _ => false
        }
        case BOTTOMINNER => mov2 match {
          case RIGHTTOP | TOPLEFT | LEFTTOP | TOPRIGHT | INNERBOTTOM => true
          case _ => false
        }
        case INNERTOP => mov2 match {
          case BOTTOMRIGHT | LEFTBOTTOM | BOTTOMLEFT | RIGHTBOTTOM | TOPINNER => true
          case _ => false
        }
        case TOPLEFT => mov2 match {
          case INNERRIGHT | LEFTBOTTOM | RIGHTINNER | INNERBOTTOM | RIGHTTOP | BOTTOMINNER => true
          case _ => false
        }
        case LEFTBOTTOM => mov2 match {
          case INNERRIGHT | TOPLEFT | RIGHTINNER | BOTTOMRIGHT | INNERTOP | TOPINNER => true
          case _ => false
        }
        case BOTTOMRIGHT => mov2 match {
          case INNERTOP | LEFTINNER | TOPINNER | RIGHTTOP | LEFTINNER | INNERLEFT => true
          case _ => false
        }
        case RIGHTBOTTOM => mov2 match {
          case INNERLEFT | TOPRIGHT | LEFTINNER | TOPINNER | INNERTOP | BOTTOMLEFT => true
          case _ => false
        }
        case TOPRIGHT => mov2 match {
          case INNERLEFT | RIGHTBOTTOM | LEFTINNER | INNERBOTTOM | LEFTTOP | BOTTOMINNER => true
          case _ => false
        }
        case LEFTTOP => mov2 match {
          case INNERBOTTOM | BOTTOMINNER | TOPRIGHT | INNERRIGHT | BOTTOMLEFT | RIGHTINNER => true
          case _ => false
        }
        case BOTTOMLEFT => mov2 match {
          case INNERTOP | RIGHTBOTTOM | TOPINNER | RIGHTINNER | INNERRIGHT | LEFTTOP => true
          case _ => false
        }
        case RIGHTTOP => mov2 match {
          case INNERLEFT | LEFTINNER | BOTTOMRIGHT | TOPLEFT | INNERBOTTOM | BOTTOMINNER => true
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
