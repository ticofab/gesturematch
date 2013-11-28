package actors

import akka.actor.{Props, Actor}
import models.{PossibleMatching, MatchingGroup, RequestToMatch}
import helpers.{RequestAnalyticsHelper, SwipeMovementHelper, ScreenPositionHelper, RequestStorageHelper}
import consts.Areas._
import play.api.Logger

class PositionMatcherActor extends Actor {

  private def deliverTo2Group(group: List[RequestToMatch]): Unit = {
    // the new request
    val r1 = group.head
    val pos1 = ScreenPositionHelper.getPosition(r1.movement, 2)

    // the existing request
    val r2 = group.tail.head
    val pos2 = ScreenPositionHelper.getPosition(r2.movement, 2)

    Logger.info(s"1st mov & pos:   ${r1.movement}    ${pos1.toString}")
    Logger.info(s"2nd mov & pos:   ${r2.movement}    ${pos2.toString}")

    r1.handlingActor ! MatchedPosition(pos1, r1.payload, List(r2.getMatcheeInfo))
    r2.handlingActor ! MatchedPosition(pos2, r2.payload, List(r1.getMatcheeInfo))
  }

  private def deliverTo4Group(group: List[RequestToMatch]): Unit = {
    // here I need to understand what's the orientation. the issue could be with movement that end or start in the middle,
    // as at that point I wouldn't be able to see where they are placed.

    // 1. filter out the one which starts with an inner position
    val partition1 = group partition (_.areaStart == INNER)
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
    val partition3 = partition2._2 partition (_.areaEnd == INNER)
    val r4 = partition3._1.head

    // 5. find third movement
    val r3 = partition3._2.head
    val pos3 = ScreenPositionHelper.getPosition(r3.movement, 4)

    Logger.info(s"1st mov & pos:   ${r1.movement}    ${pos1.toString}")
    Logger.info(s"2nd mov & pos:   ${r2.movement}    ${pos2.toString}")
    Logger.info(s"3rd mov & pos:   ${r3.movement}    ${pos3.toString}")
    Logger.info(s"4th pos:         ${pos4.toString}\n")

    r1.handlingActor ! new MatchedPosition(pos1, r1.payload, List(r2.getMatcheeInfo, r3.getMatcheeInfo, r4.getMatcheeInfo))
    r2.handlingActor ! new MatchedPosition(pos2, r2.payload, List(r1.getMatcheeInfo, r3.getMatcheeInfo, r4.getMatcheeInfo))
    r3.handlingActor ! new MatchedPosition(pos3, r3.payload, List(r1.getMatcheeInfo, r2.getMatcheeInfo, r4.getMatcheeInfo))
    r4.handlingActor ! new MatchedPosition(pos4, r4.payload, List(r1.getMatcheeInfo, r2.getMatcheeInfo, r3.getMatcheeInfo))
  }

  private def getMatchingGroup(request: RequestToMatch, existingRequests: List[RequestToMatch]): List[MatchingGroup] = {

    // get all possible matching groups for the given request
    val possibleMatchingGroups: List[PossibleMatching] = SwipeMovementHelper.getPossibleMatching(request.movement)

    // get all the existing requests that could be part of one of the possible groups
    val tmp1: List[(PossibleMatching, RequestToMatch)] = for {
      pg <- possibleMatchingGroups // for all the possible matching groups
      prevReq <- existingRequests // for all the previous requests, considered if
      reqCompatible = RequestAnalyticsHelper.requestsAreCompatible(request, prevReq) // the old request is compatible and
      hasCoolMovement = pg.necessaryMovements.contains(prevReq.movement) // its movement is necessary
      if reqCompatible && hasCoolMovement
    } yield (pg, prevReq)

    // data manipulation to get to a List[MatchingGroup]
    val tmp2: List[(PossibleMatching, List[RequestToMatch])] = tmp1.groupBy(_._1).mapValues(_.map(_._2)).toList
    val tmp3: List[(PossibleMatching, List[RequestToMatch])] = tmp2.filter(x => x._1.devicesInGroup == x._2.length + 1)
    val groups: List[MatchingGroup] = tmp3.map(x => MatchingGroup(x._1.devicesInGroup, request :: x._2))

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
        }
      }
    }
  }
}

object PositionMatcherActor {
  val props = Props(classOf[PositionMatcherActor])
}

