package actors

import traits.StringGenerator
import akka.actor.{Props, Actor}
import models._
import helpers.requests.RequestStorageHelper
import consts.Criteria
import play.api.Logger
import helpers.storage.DBHelper
import models.NewRequest

class AimMatcherActor extends Actor with StringGenerator {
  lazy val myName = this.getClass.getSimpleName

  def getMatchingRequests(request: RequestToMatch, existingRequests: List[RequestToMatch]): List[RequestToMatch] = {
    // for all requests, check that their orientation is similar. We can assume that all
    // requests here have a swipeOrientation value
    for {
      prevReq <- existingRequests
      if true // TODO: insert here check to see if they're aiming at each other
    } yield prevReq
  }

  def receive: Actor.Receive = {
    case NewRequest(request) =>
      def getNewRequestLogging(nrExistingRequests: Int, msg: String = "") = {
        s"$myName, new ${request.toString}, matching it with $nrExistingRequests existing requests --> $msg"
      }

      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingRequests(Criteria.AIM, request)

      // Try to create a match between the requests
      val matches: List[RequestToMatch] = getMatchingRequests(request, existingRequests)

      // This is where I know whether I
      //   1. haven't found anything
      //   2. have univocally identified a match
      //   3. have identified multiple matches, which is uncertainty.
      matches match {
        case Nil =>
          Logger.info(getNewRequestLogging(existingRequests.length, "no group has been found."))

          // simply update the requests storage with the new request and the filtered requests
          RequestStorageHelper.storeNewRequest(Criteria.AIM, request)

        case prevReq :: Nil =>

          // remove the group from the storage
          RequestStorageHelper.removeRequests(Criteria.AIM, List(prevReq))

          // generate a unique groupId
          val groupId = getGroupUniqueString

          Logger.info(getNewRequestLogging(existingRequests.length, s"match found: $groupId"))

          // Send a matching notification to the actors managing the corresponding devices
          val matchee1 = new Matchee(request.handlingActor, 0)
          val matchee2 = new Matchee(prevReq.handlingActor, 1)

          request.handlingActor ! Matched(matchee1, List(matchee2), groupId)
          prevReq.handlingActor ! Matched(matchee2, List(matchee1), groupId)

          DBHelper.addMatchEstablished(request.apiKey, request.appId, 2)

        case group :: tail =>
          // TODO
          Logger.info(getNewRequestLogging(existingRequests.length, s"${matches.size} groups found. uncertainty."))
      }
  }
}


object AimMatcherActor {
  val props = Props(classOf[AimMatcherActor])
}

