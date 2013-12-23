package helpers.movements

import consts.SwipeMovements._
import models.{RequestToMatch, PossibleMatching}
import consts.SwipeMovements
import consts.Areas.Areas
import scala.util.Try

object SwipeMovementHelper {

  /*
   * Translates a pair start- / end-swipe into a movement.
   */
  def swipesToMovement(swipeStart: Areas, swipeEnd: Areas): SwipeMovement = {
    val areasMix = swipeStart.toString + swipeEnd.toString
    Try(SwipeMovements.withName(areasMix)) getOrElse UNKNOWN
  }
}
