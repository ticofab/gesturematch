package actors

import akka.actor.{Props, Actor}
import models.{PossibleMatching, MatchingGroup, RequestToMatch}
import helpers.{SwipeMovementHelper, ScreenPositionHelper, RequestStorageHelper}
import consts.Areas._
import play.api.Logger

class MatcherActor extends Actor {

  private def requestsAreCompatible(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if equality parameters are the same
    val equality = r1.equalityParam == r2.equalityParam

    closeEnough && equality
  }

  private def deliverTo2Group(group: List[RequestToMatch]): Unit = {
    // the new request
    val r1 = group.head
    val pos1 = ScreenPositionHelper.getPosition(r1.movement, 2)

    // the existing request
    val r2 = group.tail.head
    val pos2 = ScreenPositionHelper.getPosition(r2.movement, 2)

    Logger.info(s"1st mov & pos:   ${r1.movement}    ${pos1.toString}")
    Logger.info(s"2nd mov & pos:   ${r2.movement}    ${pos2.toString}")

    r1.handlingActor ! Matched2(pos1, r1.payload, (r2.handlingActor, r2.payload))
    r2.handlingActor ! Matched2(pos2, r2.payload, (r1.handlingActor, r1.payload))
  }

  private def deliverTo4Group(group: List[RequestToMatch]): Unit = {
    // here I need to understand what's the orientation. the issue could be with movement that end or start in the middle,
    // as at that point I wouldn't be able to see where they are placed.

    // 1. filter out the one which starts with an inner position
    val partition1 = group partition (_.areaStart == Inner)
    val r1 = partition1._1.head

    // 2. find second request
    val secondEntrance = ScreenPositionHelper.getCorrespondingEntrance(r1.areaEnd)
    val partition2 = partition1._2 partition (_.areaStart == secondEntrance)
    val r2 = partition2._1.head

    // 3. look at what kind of movement it is and understand where the start is
    val pos1 = ScreenPositionHelper.getCorrespondingStartPosition(r2.movement)
    val pos2 = ScreenPositionHelper.getPosition(r2.movement, 4)

    // 4. find final movement
    val pos4 = ScreenPositionHelper.getCorrespondingFinalPosition(r1.movement, pos1)
    val partition3 = partition2._2 partition (_.areaEnd == Inner)
    val r4 = partition3._1.head

    // 5. find third movement
    val r3 = partition3._2.head
    val pos3 = ScreenPositionHelper.getPosition(r3.movement, 4)

    Logger.info(s"1st mov & pos:   ${r1.movement}    ${pos1.toString}")
    Logger.info(s"2nd mov & pos:   ${r2.movement}    ${pos2.toString}")
    Logger.info(s"3rd mov & pos:   ${r3.movement}    ${pos3.toString}")
    Logger.info(s"4th pos:         ${pos4.toString}\n")

    r1.handlingActor ! new Matched4(pos1, r1.payload, List(r2.getServiceInfo, r3.getServiceInfo, r4.getServiceInfo))
    r2.handlingActor ! new Matched4(pos2, r2.payload, List(r1.getServiceInfo, r3.getServiceInfo, r4.getServiceInfo))
    r3.handlingActor ! new Matched4(pos3, r3.payload, List(r1.getServiceInfo, r2.getServiceInfo, r4.getServiceInfo))
    r4.handlingActor ! new Matched4(pos4, r4.payload, List(r1.getServiceInfo, r2.getServiceInfo, r3.getServiceInfo))
  }

  private def getMatchingGroup(request: RequestToMatch, existingRequests: List[RequestToMatch]): List[MatchingGroup] = {
    // get all possible matching groups for the given request
    val possibleMatchingGroups: List[PossibleMatching] = SwipeMovementHelper.getPossibleMatching(request.movement)

    // get all the existing requests that could be part of one of the possible groups
    val tmp1: List[(PossibleMatching, RequestToMatch)] = for {
      pg <- possibleMatchingGroups
      prevReq <- existingRequests
      if requestsAreCompatible(request, prevReq) && pg.necessaryMovements.contains(prevReq.movement)
    } yield (pg, prevReq)

    // data manipulation to get to a List[MatchingGroup]
    val tmp2: List[(PossibleMatching, List[RequestToMatch])] = tmp1.groupBy(_._1).mapValues(_.map(_._2)).toList
    val tmp3: List[MatchingGroup] = tmp2.map(x => MatchingGroup(x._1.devicesInGroup, request :: x._2))
    val groups: List[MatchingGroup] = tmp3.filter(m => m.devicesInGroup == m.requests.size)

    Logger.debug(s"groups found:\n  ${groups.toString()}")
    groups
  }

  def receive: Actor.Receive = {
    case NewRequest(request) => {
      Logger.info(s"MatcherActor, new ${request.toString}")

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingRequests(request)

      // Try to create a match between the requests
      Logger.info(s"Matching it with ${existingRequests.length} existing requests.")
      val matchedGroups: List[MatchingGroup] = getMatchingGroup(request, existingRequests)

      // This is where I know whether I
      //   1. haven't found anything
      //   2. have univocally identified a group
      //   3. have identified multiple groups, which is uncertainty.
      matchedGroups match {
        case List() => {
          Logger.info(s"  --> no group has been found.")

          // simply update the requests storage with the new request and the filtered requests
          RequestStorageHelper.storeNewRequest(request)
        }

        case group :: Nil => {
          Logger.info(s"  --> one group found, size is ${group.devicesInGroup}")

          // remove the group from the storage
          RequestStorageHelper.removeRequests(group.requests)

          // Send a matching notification to the actors managing the corresponding devices
          group.devicesInGroup match {
            case 4 => deliverTo4Group(group.requests)
            case 2 => deliverTo2Group(group.requests)
          }
        }

        case group :: tail => {
          Logger.info(s"  --> more than one group found. uncertainty.")

          // TODO: send try again message
        }
      }
    }
  }
}

object MatcherActor {
  val props = Props(classOf[MatcherActor])
}

case class NewRequest(request: RequestToMatch)
