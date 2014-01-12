package actors

import akka.actor.{Actor, Props}
import play.api.Logger
import models.{NewRequest, Matched, Matchee, RequestToMatch}
import consts.Criteria
import traits.StringGenerator
import helpers.requests.RequestStorageHelper
import helpers.presence.PatternHelper
import helpers.storage.DBHelper

class PresenceMatcherActor extends Actor with StringGenerator {

  lazy val myName = this.getClass.getSimpleName

  def receive: Actor.Receive = {
    case NewRequest(request) =>

      def getNewRequestLogging(possiblyMatchingRequests: Int, msg: String = "") = {
        s"$myName, new ${request.toString}, matching it with $possiblyMatchingRequests existing requests --> $msg"
      }

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // get a list of possibly matching requests
      val possiblyMatchingRequests: List[RequestToMatch] = RequestStorageHelper.getValidExistingRequests(Criteria.PRESENCE, request)

      // try to find a touch pattern
      val group: List[RequestToMatch] = PatternHelper.getMatchedPattern(request :: possiblyMatchingRequests)

      group match {
        case Nil =>
          // no match. add the request to the storage
          Logger.info(getNewRequestLogging(possiblyMatchingRequests.size, "no match found. Adding request to the storage."))
          RequestStorageHelper.storeNewRequest(Criteria.PRESENCE, request)

        case x :: Nil =>
          // only one element. wrong!
          Logger.info(getNewRequestLogging(possiblyMatchingRequests.size, "error: group with one element."))

        case x :: xs =>
          // we identified a group!
          Logger.info(getNewRequestLogging(possiblyMatchingRequests.size, s"group found, size: ${group.size}"))
          RequestStorageHelper.removeRequests(Criteria.PRESENCE, group)

          // get unique group id
          val groupId = getGroupUniqueString

          val matcheesInfo: List[Matchee] = group.zipWithIndex.map(x => Matchee(x._1.handlingActor, x._2))
          group.foreach(r => {
            // this could maybe be done by each actor, but this way it's cleaner
            val (myInfo, othersInfo) = matcheesInfo.partition(m => m.handlingActor == r.handlingActor)
            r.handlingActor ! Matched(myInfo.head, othersInfo, groupId)
          })

          DBHelper.addMatchEstablished(request.apiKey, request.appId, group.length)

      }
  }
}

object PresenceMatcherActor {
  val props = Props(classOf[PresenceMatcherActor])
}
