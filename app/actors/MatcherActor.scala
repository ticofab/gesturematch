package actors

import akka.actor.Actor
import models.RequestToMatch
import helpers.{MovementComparator, RequestListHelper}
import consts.TimeoutConsts
import models.SwipeMovement._
import com.sun.org.apache.bcel.internal.classfile.Unknown

/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 17:51
 */
class MatcherActor extends Actor {

  // Returns true if the two requests could belong to the same connection
  def matchRequests(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if movements are compatible
    val mov1: SwipeMovement = MatcherActor.swipesToMovement(r1.swipeStart, r1.swipeEnd)
    val mov2: SwipeMovement = MatcherActor.swipesToMovement(r1.swipeStart, r2.swipeEnd)
    val compatibleMovements: Boolean = MovementComparator.compareMovement(mov1, mov2) != 0

    closeEnough && compatibleMovements
  }

  // Given a new request and a list of existing ones, it possibly returns a group of matching requests.
  def extractMatchingGroup(request: RequestToMatch, existingRequests: List[RequestToMatch]): Option[List[RequestToMatch]] = {
    val matchingRequests: List[RequestToMatch] = existingRequests filter (existingRequest => matchRequests(request, existingRequest))
    matchingRequests match {
      case List() => None
      case _ => Some(request :: matchingRequests)
    }
  }

  // Delivers the matched messages to all the involved actors
  def deliverMatchedMessages(group: List[RequestToMatch]): Unit = {
    val devicesInConnection: Int = group length



  }

  def receive: Actor.Receive = {
    case NewRequest(request) => {

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain existing requests and filter by time
      val maxOldestRequestTS = request.timestamp - TimeoutConsts.maxOldestRequestTOMillis
      val existingRequests = RequestListHelper.getExistingRequests filter (previousRequest => previousRequest.timestamp >= maxOldestRequestTS)

      // Try to create a match between the requests
      val matchedRequests: Option[List[RequestToMatch]] = extractMatchingGroup(request, existingRequests)

      // do we have a group back?
      matchedRequests match {
        case None => {
          // If nothing has changed, this is useless for now.
          RequestListHelper updateCurrentRequests existingRequests
        }
        case Some(group) => {
          // update the requests storage with the churned list
          RequestListHelper updateCurrentRequests (existingRequests diff group)

          // Send a matching notification to the actors managing the corresponding devices
          deliverMatchedMessages(group)
        }
      }
    }
  }
}

object MatcherActor {
  val kViewAreaTop: Int = 0
  val kViewAreaBottom: Int = 1
  val kViewAreaLeft: Int = 2
  val kViewAreaRight: Int = 3
  val kViewAreaInner: Int = 4
  val kViewAreaInvalid: Int = 5

  def swipesToMovement(swipeStart: Int, swipeEnd: Int): SwipeMovement = {
    val swipeValue: Int = swipeStart * 10 + swipeEnd
    swipeValue match {
      case 2 => TopLeft
      case 3 => TopRight
      case 4 => TopInner
      case 12 => BottomLeft
      case 13 => BottomRight
      case 14 => BottomInner
      case 20 => LeftTop
      case 21 => LeftBottom
      case 24 => LeftInner
      case 30 => RightTop
      case 31 => RightBottom
      case 34 => RightInner
      case 40 => InnerTop
      case 41 => InnerBottom
      case 42 => InnerLeft
      case 43 => InnerRight
      case _ => Unknown
    }
  }
}


case class NewRequest(request: RequestToMatch)
