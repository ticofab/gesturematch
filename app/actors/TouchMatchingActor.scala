package actors

import akka.actor.{Actor, Props}
import helpers.{SwipeMovementHelper, RequestAnalyticsHelper, RequestStorageHelper}
import play.api.Logger
import models.RequestToMatch
import consts.Criteria

class TouchMatchingActor extends Actor {

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
      val group: List[RequestToMatch] = SwipeMovementHelper.getMatchedPattern(possiblyMatchingRequests)

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

          val matcheesInfo = group.map(x => x.getMatcheeInfo)
          group.foreach(r => r.handlingActor ! MatchedGroup(matcheesInfo))
        }
      }
    }
  }
}

object TouchMatchingActor {
  val props = Props(classOf[TouchMatchingActor])
}
