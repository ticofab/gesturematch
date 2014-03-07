package actors

import akka.actor.{Actor, Props}
import models._
import helpers.requests.RequestStorageHelper
import consts.Criteria
import play.api.Logger
import consts.SwipeMovements.SwipeMovement
import traits.StringGenerator
import models.NewRequest
import helpers.storage.DBHelper
import helpers.matchers.pinch.PinchMatchingHelper
import helpers.matchers.swipe.PatternHelper

class PinchMatcherActor extends Actor with StringGenerator {
  lazy val myName = this.getClass.getSimpleName

  private def getMatchingGroup(request: RequestToMatch, existingRequests: List[RequestToMatch]): List[RequestToMatch] = {

    // get the possible matching movement for the given request
    val matchingMovement: SwipeMovement = PinchMatchingHelper.getMatchingMovement(request.movement)

    // get all the existing requests that could be part of one of the possible groups
    val matches: List[RequestToMatch] = for {
      prevReq <- existingRequests // for all the previous requests, considered if
      if prevReq.movement == matchingMovement // its movement is necessary
    } yield prevReq

    Logger.debug(s"$myName, groups found:\n  $matches")
    matches
  }

  def receive: Actor.Receive = {
    case NewRequest(request) =>
      def getNewRequestLogging(nrExistingRequests: Int, msg: String = "") = {
        s"$myName, new ${request.toString}, matching it with $nrExistingRequests existing requests --> $msg"
      }

      // This operation should be fine, as the MatcherActor is designed to be a single one,
      // therefore there won't be two concurrent accesses to the RequestListHelper?

      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingRequests(Criteria.PINCH, request)

      // Try to create a match between the requests
      val matches: List[RequestToMatch] = getMatchingGroup(request, existingRequests)

      // This is where I know whether I
      //   1. haven't found anything
      //   2. have univocally identified a match
      //   3. have identified multiple matches, which is uncertainty.
      matches match {
        case Nil =>
          Logger.info(getNewRequestLogging(existingRequests.length, "no group has been found."))

          // simply update the requests storage with the new request and the filtered requests
          RequestStorageHelper.storeNewRequest(Criteria.PINCH, request)

        case prevReq :: Nil =>

          // remove the group from the storage
          RequestStorageHelper.removeRequests(Criteria.PINCH, List(prevReq))

          // generate a unique groupId
          val groupId = getGroupUniqueString

          Logger.info(getNewRequestLogging(existingRequests.length, s"match found: $groupId"))

          // Send a matching notification to the actors managing the corresponding devices
          val scheme: Scheme = new Scheme
          val id1: Int = scheme.addFirstDevice
          val id2: Int = scheme.addDevice(PatternHelper.getDeviceSchemePosition(prevReq.areaEnd), id1)
          val matchee1 = new Matchee(request.handlingActor, id1)
          val matchee2 = new Matchee(prevReq.handlingActor, id2)

          val message = new Matched(List(matchee1, matchee2), groupId, Some(scheme))

          request.handlingActor ! message
          prevReq.handlingActor ! message

          DBHelper.addMatchEstablished(request.apiKey, request.appId, 2)

        case group :: tail =>
          // TODO
          Logger.info(getNewRequestLogging(existingRequests.length, s"${matches.size} groups found. uncertainty."))

      }
  }
}

object PinchMatcherActor {
  val props = Props(classOf[PinchMatcherActor])
}
