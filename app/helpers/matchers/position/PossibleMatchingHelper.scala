package helpers.position

import consts.SwipeMovements._
import models.PossibleMatching

object PossibleMatchingHelper {
  private def makePossibleMatchingListTwoAndFour(pos2: SwipeMovement,
                                                 pos4common: SwipeMovement,
                                                 pos4first1: SwipeMovement,
                                                 pos4first2: SwipeMovement,
                                                 pos4second1: SwipeMovement,
                                                 pos4second2: SwipeMovement) = {
    val pm1 = new PossibleMatching(2, List(pos2))
    val pm2 = new PossibleMatching(4, List(pos4common, pos4first1, pos4first2))
    val pm3 = new PossibleMatching(4, List(pos4common, pos4second1, pos4second2))
    List(pm1, pm2, pm3)
  }

  private def makePossibleMatchingListFour(pos4first1: SwipeMovement,
                                           pos4first2: SwipeMovement,
                                           pos4first3: SwipeMovement,
                                           pos4second1: SwipeMovement,
                                           pos4second2: SwipeMovement,
                                           pos4second3: SwipeMovement) = {
    val pm1 = new PossibleMatching(4, List(pos4first1, pos4first2, pos4first3))
    val pm2 = new PossibleMatching(4, List(pos4second1, pos4second2, pos4second3))
    List(pm1, pm2)
  }

  /*
   * Given one movement, returns the possible combinations. Limited to
   *    - 2 or 4 devices
   *    - devices all aligned according to the vertical side, ie they all "point" in the same direction
   */
  def getPossibleMatching(movement: SwipeMovement): List[PossibleMatching] = {
    movement match {

      // groups are possible with 2 and 4 devices combination
      case INNERRIGHT => makePossibleMatchingListTwoAndFour(LEFTINNER, RIGHTINNER, LEFTBOTTOM, TOPLEFT, LEFTTOP, BOTTOMLEFT)
      case LEFTINNER => makePossibleMatchingListTwoAndFour(INNERRIGHT, INNERLEFT, RIGHTBOTTOM, TOPRIGHT, RIGHTTOP, BOTTOMRIGHT)
      case RIGHTINNER => makePossibleMatchingListTwoAndFour(INNERLEFT, INNERRIGHT, LEFTBOTTOM, TOPLEFT, LEFTTOP, BOTTOMLEFT)
      case INNERLEFT => makePossibleMatchingListTwoAndFour(RIGHTINNER, LEFTINNER, RIGHTTOP, BOTTOMRIGHT, RIGHTBOTTOM, TOPRIGHT)
      case INNERBOTTOM => makePossibleMatchingListTwoAndFour(TOPINNER, BOTTOMINNER, TOPRIGHT, LEFTTOP, TOPLEFT, RIGHTTOP)
      case TOPINNER => makePossibleMatchingListTwoAndFour(INNERBOTTOM, INNERTOP, BOTTOMLEFT, RIGHTBOTTOM, BOTTOMRIGHT, LEFTBOTTOM)
      case BOTTOMINNER => makePossibleMatchingListTwoAndFour(INNERTOP, INNERBOTTOM, TOPRIGHT, LEFTTOP, TOPLEFT, RIGHTTOP)
      case INNERTOP => makePossibleMatchingListTwoAndFour(BOTTOMINNER, TOPINNER, BOTTOMRIGHT, LEFTBOTTOM, BOTTOMLEFT, RIGHTBOTTOM)

      // groups are possible only with 4 devices
      case TOPLEFT => makePossibleMatchingListFour(INNERRIGHT, LEFTBOTTOM, RIGHTINNER, INNERBOTTOM, RIGHTTOP, BOTTOMINNER)
      case LEFTBOTTOM => makePossibleMatchingListFour(INNERRIGHT, TOPLEFT, RIGHTINNER, INNERTOP, BOTTOMRIGHT, TOPINNER)
      case BOTTOMRIGHT => makePossibleMatchingListFour(INNERTOP, LEFTBOTTOM, TOPINNER, INNERLEFT, RIGHTTOP, LEFTINNER)
      case RIGHTBOTTOM => makePossibleMatchingListFour(INNERLEFT, TOPRIGHT, LEFTINNER, INNERTOP, BOTTOMLEFT, TOPINNER)
      case TOPRIGHT => makePossibleMatchingListFour(INNERLEFT, RIGHTBOTTOM, LEFTINNER, INNERBOTTOM, LEFTTOP, BOTTOMINNER)
      case LEFTTOP => makePossibleMatchingListFour(INNERBOTTOM, TOPRIGHT, BOTTOMINNER, INNERRIGHT, BOTTOMLEFT, RIGHTINNER)
      case BOTTOMLEFT => makePossibleMatchingListFour(INNERTOP, RIGHTBOTTOM, TOPINNER, INNERRIGHT, LEFTTOP, RIGHTINNER)
      case RIGHTTOP => makePossibleMatchingListFour(INNERLEFT, BOTTOMRIGHT, LEFTINNER, INNERBOTTOM, TOPLEFT, BOTTOMINNER)

      case _ => List(new PossibleMatching(0, List()))
    }
  }
}
