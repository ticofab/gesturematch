package actors

import akka.actor.{Props, Actor}
import models._
import consts.Areas._
import play.api.Logger
import consts.Criteria
import traits.StringGenerator
import helpers.position.{PossibleMatchingHelper, ScreenPositionHelper}
import helpers.requests.RequestStorageHelper
import helpers.storage.DBHelper

class PositionMatcherActor extends Actor with StringGenerator {

  lazy val myName = this.getClass.getSimpleName

  private def deliverTo2Group(group: List[RequestToMatch], groupId: String): Unit = {
    // the new request
    val r1 = group.head
    val pos1 = ScreenPositionHelper.getPosition(r1.movement, 2)

    // the existing request
    val r2 = group.tail.head
    val pos2 = ScreenPositionHelper.getPosition(r2.movement, 2)

    Logger.info(s"$myName, 1st mov & pos:   ${r1.movement}    ${pos1.toString}")
    Logger.info(s"$myName, 2nd mov & pos:   ${r2.movement}    ${pos2.toString}")

    val matchee1 = Matchee(r1.handlingActor, 0, pos1)
    val matchee2 = Matchee(r2.handlingActor, 1, pos2)

    r1.handlingActor ! Matched(matchee1, List(matchee2), groupId)
    r2.handlingActor ! Matched(matchee2, List(matchee1), groupId)

    DBHelper.addMatchEstablished(r1.apiKey, r1.appId, 2)
  }

  private def deliverTo4Group(group: List[RequestToMatch], groupId: String): Unit = {
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

    Logger.info(s"$myName, 1st mov & pos:   ${r1.movement}    ${pos1.toString}")
    Logger.info(s"$myName, 2nd mov & pos:   ${r2.movement}    ${pos2.toString}")
    Logger.info(s"$myName, 3rd mov & pos:   ${r3.movement}    ${pos3.toString}")
    Logger.info(s"$myName, 4th pos:         ${pos4.toString}\n")

    val matchee1 = Matchee(r1.handlingActor, 0, pos1)
    val matchee2 = Matchee(r2.handlingActor, 1, pos2)
    val matchee3 = Matchee(r3.handlingActor, 2, pos3)
    val matchee4 = Matchee(r4.handlingActor, 3, pos4)

    r1.handlingActor ! Matched(matchee1, List(matchee2, matchee3, matchee4), groupId)
    r2.handlingActor ! Matched(matchee2, List(matchee1, matchee3, matchee4), groupId)
    r3.handlingActor ! Matched(matchee3, List(matchee1, matchee2, matchee4), groupId)
    r4.handlingActor ! Matched(matchee4, List(matchee1, matchee2, matchee4), groupId)

    DBHelper.addMatchEstablished(r1.apiKey, r1.appId, 4)
  }

  private def getMatchingGroup(request: RequestToMatch, existingRequests: List[RequestToMatch]): List[MatchingGroup] = {

    // get all possible matching groups for the given request
    val possibleMatchingGroups: List[PossibleMatching] = PossibleMatchingHelper.getPossibleMatching(request.movement)

    // get all the existing requests that could be part of one of the possible groups
    val tmp1: List[(PossibleMatching, RequestToMatch)] = for {
      pg <- possibleMatchingGroups // for all the possible matching groups
      prevReq <- existingRequests // for all the previous requests, considered if
      if pg.necessaryMovements.contains(prevReq.movement) // its movement is necessary
    } yield (pg, prevReq)

    // data manipulation to get to a List[MatchingGroup]
    val tmp2: List[(PossibleMatching, List[RequestToMatch])] = tmp1.groupBy(_._1).mapValues(_.map(_._2)).toList
    val tmp3: List[(PossibleMatching, List[RequestToMatch])] = tmp2.filter(x => x._1.devicesInGroup == x._2.length + 1)
    val groups: List[MatchingGroup] = tmp3.map(x => MatchingGroup(x._1.devicesInGroup, request :: x._2))

    Logger.debug(s"$myName, groups found:\n  ${groups.toString()}")
    groups
  }

  def receive: Actor.Receive = {
    case NewRequest(request) =>
      def getNewRequestLogging(nrExistingRequests: Int, msg: String = "") = {
        s"$myName, new ${request.toString}, matching it with $nrExistingRequests existing requests --> $msg"
      }

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingRequests(Criteria.POSITION, request)

      // Try to create a match between the requests
      val matchedGroups: List[MatchingGroup] = getMatchingGroup(request, existingRequests)

      // This is where I know whether I
      //   1. haven't found anything
      //   2. have univocally identified a group
      //   3. have identified multiple groups, which is uncertainty.
      matchedGroups match {
        case Nil =>
          Logger.info(getNewRequestLogging(existingRequests.length, "no group has been found."))

          // simply update the requests storage with the new request and the filtered requests
          RequestStorageHelper.storeNewRequest(Criteria.POSITION, request)

        case group :: Nil =>

          // remove the group from the storage
          RequestStorageHelper.removeRequests(Criteria.POSITION, group.requests)

          // generate a unique groupId
          val groupId = getGroupUniqueString

          Logger.info(getNewRequestLogging(existingRequests.length, s"group formed: $groupId, size: ${group.devicesInGroup}"))

          // Send a matching notification to the actors managing the corresponding devices
          group.devicesInGroup match {
            case 4 => deliverTo4Group(group.requests, groupId)
            case 2 => deliverTo2Group(group.requests, groupId)
          }

        case group :: tail =>
          Logger.info(getNewRequestLogging(existingRequests.length, s"${matchedGroups.size} groups found. uncertainty."))
      }
  }
}

object PositionMatcherActor {
  val props = Props(classOf[PositionMatcherActor])
}

