package actors

import akka.actor.{Props, Actor}
import models.RequestToMatch
import helpers.{SwipeMovementHelper, ScreenPositionHelper, MovementComparator, RequestListHelper}
import consts.Timeouts
import consts.Areas._
import SwipeMovementHelper._
import consts.SwipeMovements.SwipeMovement
import play.api.Logger

class MatcherActor extends Actor {

  // Returns true if the two requests could belong to the same connection
  def matchRequests(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if movements are compatible
    val mov1: SwipeMovement = swipesToMovement(r1.swipeStart, r1.swipeEnd)
    val mov2: SwipeMovement = swipesToMovement(r1.swipeStart, r2.swipeEnd)
    val compatibleMovements: Boolean = MovementComparator.compareMovement(mov1, mov2) != 0

    // check if equality parameters are the same
    val equality = r1.equalityParam == r2.equalityParam

    closeEnough && compatibleMovements && equality
  }

  // Given a new request and a list of existing ones, it possibly returns a group of matching requests.
  def extractMatchingGroup(request: RequestToMatch, existingRequests: List[RequestToMatch]): Option[List[RequestToMatch]] = {
    val matchingRequests: List[RequestToMatch] = existingRequests filter (matchRequests(request, _))
    matchingRequests match {
      case List() => None
      case _ => Some(request :: matchingRequests)
    }
  }

  def deliverTo2Group(group: List[RequestToMatch]): Unit = {
    // the new request
    val r1 = group.head
    val mov1 = swipesToMovement(r1.swipeStart, r1.swipeEnd)
    val pos1 = ScreenPositionHelper.getPosition(mov1, 2)

    // the existing request
    val r2 = group.tail.head
    val mov2 = swipesToMovement(r2.swipeStart, r2.swipeEnd)
    val pos2 = ScreenPositionHelper.getPosition(mov2, 2)

    Logger.info(s"1st mov & pos:   ${mov1.toString}    ${pos1.toString}")
    Logger.info(s"2nd mov & pos:   ${mov2.toString}    ${pos2.toString}")

    r1.handlingActor ! Matched2(pos1, r1.payload, (r2.handlingActor, r2.payload))
    r2.handlingActor ! Matched2(pos2, r2.payload, (r1.handlingActor, r1.payload))
  }

  def deliverTo4Group(group: List[RequestToMatch]): Unit = {
    // here I need to understand what's the orientation. the issue could be with movement that end or start in the middle,
    // as at that point I wouldn't be able to see where they are placed.

    // 1. filter out the one which starts with an inner position
    val partition1 = group partition (_.swipeStart == Inner)
    val r1 = partition1._1.head
    val mov1 = swipesToMovement(r1.swipeStart, r1.swipeEnd)

    // 2. find second request
    val secondEntrance = ScreenPositionHelper.getCorrespondingEntrance(r1.swipeEnd)
    val partition2 = partition1._2 partition (_.swipeStart == secondEntrance)
    val r2 = partition2._1.head

    // 3. look at what kind of movement it is and understand where the start is
    val mov2 = swipesToMovement(r2.swipeStart, r2.swipeEnd)
    val pos1 = ScreenPositionHelper.getCorrespondingStartPosition(mov2)
    val pos2 = ScreenPositionHelper.getPosition(mov2, 4)

    // 4. find final movement
    val pos4 = ScreenPositionHelper.getCorrespondingFinalPosition(mov1, pos1)
    val partition3 = partition2._2 partition (_.swipeEnd == Inner)
    val r4 = partition3._1.head

    // 5. find third movement
    val r3 = partition3._2.head
    val mov3 = swipesToMovement(r3.swipeStart, r3.swipeEnd)
    val pos3 = ScreenPositionHelper.getPosition(mov3, 4)

    Logger.info(s"1st mov & pos:   ${mov1.toString}    ${pos1.toString}")
    Logger.info(s"2nd mov & pos:   ${mov2.toString}    ${pos2.toString}")
    Logger.info(s"3rd mov & pos:   ${mov3.toString}    ${pos3.toString}")
    Logger.info(s"4th pos:         ${pos4.toString}\n")

    r1.handlingActor ! new Matched4(pos1, r1.payload, List(r2.getServiceInfo, r3.getServiceInfo, r4.getServiceInfo))
    r2.handlingActor ! new Matched4(pos2, r2.payload, List(r1.getServiceInfo, r3.getServiceInfo, r4.getServiceInfo))
    r3.handlingActor ! new Matched4(pos3, r3.payload, List(r1.getServiceInfo, r2.getServiceInfo, r4.getServiceInfo))
    r4.handlingActor ! new Matched4(pos4, r4.payload, List(r1.getServiceInfo, r2.getServiceInfo, r3.getServiceInfo))
  }

  def receive: Actor.Receive = {
    case NewRequest(request) => {
      Logger.info(s"MatcherActor, new request ${request.toString}")

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain existing requests and filter by time
      val maxOldestRequestTS = request.timestamp - Timeouts.maxOldestRequestTOMillis
      val existingRequests = RequestListHelper.getExistingRequests filter (_.timestamp >= maxOldestRequestTS)

      // Try to create a match between the requests
      val matchedRequests: Option[List[RequestToMatch]] = extractMatchingGroup(request, existingRequests)

      // do we have a group back?
      matchedRequests match {
        case None => {
          Logger.info(s"Matching it with existing requests: no group has been found.")

          // simply update the requests storage with the new request and the filtered requests
          RequestListHelper updateCurrentRequests request :: existingRequests
        }
        case Some(group) => {
          Logger.info(s"Matching it with existing requests: group found, size is ${group.length}")

          // update the requests storage with the churned list
          RequestListHelper updateCurrentRequests (existingRequests diff group)

          // Send a matching notification to the actors managing the corresponding devices
          group.length match {
            case 4 => deliverTo4Group(request :: group)
            case 2 => deliverTo2Group(request :: group)
          }
        }
      }
    }
  }
}

object MatcherActor {
  val props = Props(classOf[MatcherActor])
}

case class NewRequest(request: RequestToMatch)
