package actors

import akka.actor.{Actor, Props}
import helpers.{RequestAnalyticsHelper, RequestStorageHelper}
import play.api.Logger
import models.{NewRequest, Matched, Matchee, RequestToMatch}
import consts.Criteria
import helpers.movements.SwipeMovementHelper
import traits.StringGenerator

class TouchMatchingActor extends Actor with StringGenerator {

  lazy val myName = this.getClass.getSimpleName

  def receive: Actor.Receive = {
    case NewRequest(request) => {
      Logger.info(s"$myName, new ${request.toString}")

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain valid existing requests
      val existingRequests = RequestStorageHelper.getValidExistingRequests(Criteria.PRESENCE, request)

      // get a list of possibly matching requests
      val possiblyMatchingRequests: List[RequestToMatch] = for {
        existingRequest <- existingRequests
        if RequestAnalyticsHelper.requestsAreCompatible(request, existingRequest)
      } yield (existingRequest)


      // try to find a touch pattern
      val group: List[RequestToMatch] = SwipeMovementHelper.getMatchedPattern(request :: possiblyMatchingRequests)

      group match {
        case Nil => {
          // no match. add the request to the storage
          Logger.info(s"$myName, no match found. Adding request to the storage.")
          RequestStorageHelper.storeNewRequest(Criteria.PRESENCE, request)
        }

        case x :: Nil => {
          // only one element. wrong!
          Logger.info(s"$myName, error: group with one element.")
        }

        case x :: xs => {
          // we identified a group!
          Logger.info(s"$myName, group found, size: ${group.size}")
          RequestStorageHelper.removeRequests(Criteria.PRESENCE, group)

          // get unique group id
          val groupId = getGroupUniqueString

          val matcheesInfo: List[Matchee] = group.zipWithIndex.map(x => Matchee(x._1.handlingActor, x._2))
          group.foreach(r => {
            // this could maybe be done by each actor, but this way it's cleaner
            val (myInfo, othersInfo) = matcheesInfo.partition(m => m.handlingActor == r.handlingActor)
            r.handlingActor ! Matched(myInfo.head, othersInfo, groupId)
          })
        }
      }
    }
  }
}

object TouchMatchingActor {
  val props = Props(classOf[TouchMatchingActor])
}
