package actors

import akka.actor.{Actor, Props}
import helpers.{SwipeMovementHelper, RequestAnalyticsHelper, RequestStorageHelper}
import play.api.Logger
import models.RequestToMatch

class TouchMatchingActor extends Actor {

  def receive: Actor.Receive = {
    case NewRequest(request) => {
      Logger.info(s"MatcherActor, new ${request.toString}")

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain valid existing requests
      val existingRequests = RequestStorageHelper.getValidExistingRequests(request)

      // get a list of possibly matching requests
      val possiblyMatchingRequests: List[RequestToMatch] = for {
        existingRequest <- existingRequests
        if RequestAnalyticsHelper.requestsAreCompatible(request, existingRequest)
      } yield (existingRequest)

      // try to find a touch pattern
      val requestsInPattern: List[RequestToMatch] = SwipeMovementHelper.getMatchedPattern(possiblyMatchingRequests)

      requestsInPattern match {
        case Nil => {
          // no match. add the request to the storage
        }

        case x :: Nil => {
          // only one element. wrong!
        }

        case x :: xs => {
          // we identified a group!
          val matcheesInfo = requestsInPattern.map(x => x.getMatcheeInfo)
          requestsInPattern.foreach(r => r.handlingActor ! MatchedGroup(matcheesInfo))
        }
      }
    }
  }
}

object TouchMatchingActor {
  val props = Props(classOf[TouchMatchingActor])
}
