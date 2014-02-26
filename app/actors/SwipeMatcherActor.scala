package actors

import akka.actor.{Actor, Props}
import play.api.Logger
import models._
import consts.Criteria
import traits.StringGenerator
import helpers.requests.RequestStorageHelper
import helpers.storage.DBHelper
import models.NewRequest
import models.Matched
import helpers.matchers.swipe.PatternHelper

class SwipeMatcherActor extends Actor with StringGenerator {

  lazy val myName = this.getClass.getSimpleName

  def receive: Actor.Receive = {
    case NewRequest(request) =>

      def getNewRequestLogging(possiblyMatchingRequests: Int, msg: String = "") = {
        s"$myName, new ${request.toString}, matching it with $possiblyMatchingRequests existing requests --> $msg"
      }

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // get a list of possibly matching requests
      val possiblyMatchingRequests: List[RequestToMatch] = RequestStorageHelper.getValidExistingRequests(Criteria.SWIPE, request)

      // try to find a matching pattern
      val (matches, isUnique): (List[RequestToMatch], Boolean) = PatternHelper.getMatchedPattern(request :: possiblyMatchingRequests)

      matches match {
        case Nil =>
          // no match. add the request to the storage
          Logger.info(getNewRequestLogging(possiblyMatchingRequests.size, "no match found. Adding request to the storage."))
          RequestStorageHelper.storeNewRequest(Criteria.SWIPE, request)

        case x :: Nil =>
          // only one element. wrong!
          Logger.info(getNewRequestLogging(possiblyMatchingRequests.size, "error: group with one element."))

        case x :: xs =>
          // we identified a group!
          Logger.info(getNewRequestLogging(possiblyMatchingRequests.size, s"group found, size: ${matches.size}"))
          RequestStorageHelper.removeRequests(Criteria.SWIPE, matches)

          // get unique group id
          val groupId = getGroupUniqueString

          val zippedMatches = matches.zipWithIndex
          val matcheesInfo: List[Matchee] = zippedMatches.map(x => Matchee(x._1.handlingActor, x._2))

          // send a position scheme if available
          val scheme: Option[List[DeviceInScheme]] = if (isUnique) {
            Some(zippedMatches.map(x => DeviceInScheme(PatternHelper.getDeviceSchemePosition(x._1.areaStart), x._2)))
          } else {
            None
          }

          matches.foreach(r => {
            // this could maybe be done by each actor, but this way it's cleaner
            val (myInfo, othersInfo) = matcheesInfo.partition(m => m.handlingActor == r.handlingActor)
            r.handlingActor ! Matched(myInfo.head, othersInfo, groupId, scheme)
          })

          DBHelper.addMatchEstablished(request.apiKey, request.appId, matches.length)
      }
  }
}

object SwipeMatcherActor {
  val props = Props(classOf[SwipeMatcherActor])
}
