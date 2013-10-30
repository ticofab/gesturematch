package actors

import akka.actor.Actor
import models.{ScreenPosition, RequestToMatch}
import helpers.{MovementComparator, RequestListHelper}
import consts.TimeoutConsts
import models.SwipeMovement._

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
    val mov1: SwipeMovement = swipesToMovement(r1.swipeStart, r1.swipeEnd)
    val mov2: SwipeMovement = swipesToMovement(r1.swipeStart, r2.swipeEnd)
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

    devicesInConnection match {
      case 4 => deliverMatchedMessagesTo4Group(group)
      case 2 => deliverMatchedMessagesTo2Group(group)
    }
  }

  def deliverMatchedMessagesTo2Group(group: List[RequestToMatch]): Unit = {
    // the new request
    val newRequest = group head
    val mov1 = swipesToMovement(newRequest.swipeStart, newRequest.swipeEnd)
    val pos1 = ScreenPosition.getPosition(mov1, 2)

    // the existing request
    val existingRequest = (group tail) head
    val mov2 = swipesToMovement(existingRequest.swipeStart, existingRequest.swipeEnd)
    val pos2 = ScreenPosition.getPosition(mov2, 2)

    newRequest.handlingActor ! Matched2(pos1, newRequest.payload, existingRequest.payload)
    existingRequest.handlingActor ! Matched2(pos2, existingRequest.payload, newRequest.payload)
  }

  def deliverMatchedMessagesTo4Group(group: List[RequestToMatch]): Unit = {
    // here I need to understand what's the orientation. the issue could be with movement that end or start in the middle,
    // as at that point I wouldn't be able to see where they are placed.

    // 1. filter out the one which starts with an inner position
    val start_rest_partitioned_requests = group partition (request => request.swipeStart == MatcherActor.kViewAreaInner)
    val first = start_rest_partitioned_requests._1.head
    val firstMovement = swipesToMovement(first.swipeStart, first.swipeEnd)

    // 2. find second request
    val secondEntrance = MatcherActor.getCorrespondingEntrance(first.swipeEnd)
    val second_rest_partitioned_requests = start_rest_partitioned_requests._2 partition (request => request.swipeStart == secondEntrance)
    val second = second_rest_partitioned_requests._1.head

    // 3. look at what kind of movement it is and understand where the start is
    val secondMovement = swipesToMovement(second.swipeStart, second.swipeEnd)
    val firstPosition = ScreenPosition.getCorrespondingStartPosition(secondMovement)
    val secondPosition = ScreenPosition.getPosition(secondMovement, 4)

    // 4. find final movement
    val lastPosition = ScreenPosition.getCorrespondingFinalPosition(firstMovement, firstPosition)
    val last_third_partitioned_requests = second_rest_partitioned_requests._2 partition (request => request.swipeEnd == MatcherActor.kViewAreaInner)
    val last = last_third_partitioned_requests._1.head

    // 5. find third movement
    val third = last_third_partitioned_requests._2.head
    val thirdMovement = swipesToMovement(third.swipeStart, third.swipeEnd)
    val thirdPosition = ScreenPosition.getPosition(thirdMovement, 4)

    // TODO: setup a logger
    println(s"1st mov & pos:   ${firstMovement.toString}    ${firstPosition.toString}")
    println(s"2nd mov & pos:   ${secondMovement.toString}   ${secondPosition.toString}")
    println(s"3rd mov & pos:   ${thirdMovement.toString}    ${thirdPosition.toString}")
    println(s"4th pos:         ${lastPosition.toString}\n")

    first.handlingActor ! new Matched4(firstPosition, first.payload, List(second.payload, third.payload, last.payload))
    second.handlingActor ! new Matched4(secondPosition, second.payload, List(first.payload, third.payload, last.payload))
    third.handlingActor ! new Matched4(thirdPosition, third.payload, List(first.payload, second.payload, last.payload))
    last.handlingActor ! new Matched4(lastPosition, last.payload, List(first.payload, second.payload, third.payload))

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
          RequestListHelper updateCurrentRequests request :: existingRequests
        }
        case Some(group) => {
          // update the requests storage with the churned list
          RequestListHelper updateCurrentRequests (existingRequests diff group)

          // Send a matching notification to the actors managing the corresponding devices
          deliverMatchedMessages(request :: group)
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

  def getCorrespondingEntrance(exitArea: Int) =
    exitArea match {
      case `kViewAreaBottom` => kViewAreaTop
      case `kViewAreaTop` => kViewAreaBottom
      case `kViewAreaLeft` => kViewAreaRight
      case `kViewAreaRight` => kViewAreaLeft
      case _ => kViewAreaInvalid
    }
}


case class NewRequest(request: RequestToMatch)
