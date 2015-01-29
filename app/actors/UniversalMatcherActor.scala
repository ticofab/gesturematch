package actors

import akka.actor.{Actor, Props}
import consts.MatchCriteria
import consts.MatchCriteria.MatchCriteria
import helpers.logging.LoggingHelper
import helpers.matchers.pinch.PinchMatchingHelper
import helpers.matchers.swipe.SwipeMatchingHelper
import helpers.requests.RequestStorageHelper
import models.matching.base.MatchRequest
import models.messages.actors.{NewComRequest, NewCreateRequest}
import play.api.Logger
import traits.StringGenerator

class UniversalMatcherActor extends Actor {

  def receive: Actor.Receive = {
    case NewComRequest(request) =>
      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingComRequests(MatchCriteria.UNIVERSAL, request)
      UniversalMatcherActor.doTheMatching(MatchCriteria.UNIVERSAL, request, existingRequests)

    case NewCreateRequest(request) =>
      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingCreateRequests(MatchCriteria.UNIVERSAL, request)
      UniversalMatcherActor.doTheMatching(MatchCriteria.UNIVERSAL, request, existingRequests)
  }
}

object UniversalMatcherActor {
  val props = Props(classOf[UniversalMatcherActor])

  lazy val myName = this.getClass.getSimpleName

  def doTheMatching(criteria: MatchCriteria, request: MatchRequest, existingRequests: List[MatchRequest]) = {
    // at this point I should check whether either Pinch or Swipe match
    val (swipeMatches, isPatternUnique) = SwipeMatchingHelper.getMatchedPattern(request :: existingRequests)
    val pinchMatches = PinchMatchingHelper.getMatchingGroup(request, existingRequests)

    // 1. if both are non-empty --> error
    // 2. if both are emtpy --> no group found
    // 3. if one only is empty --> good

    if (swipeMatches.isEmpty && pinchMatches.isEmpty) {
      // no group found, add request to the storage
      // no match. add the request to the storage
      Logger.debug(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, "no match found."))
      RequestStorageHelper.storeNewRequest(MatchCriteria.UNIVERSAL, request)

    } else if (swipeMatches.nonEmpty && pinchMatches.nonEmpty) {
      // both found matches. uncertainty!
      Logger.debug(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, "both pinch and swipe group found!"))
      // TODO

    } else {
      // only one has found a group, good.
      if (swipeMatches.nonEmpty) {
        // we're working with swipe
        Logger.debug(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, "found a swipe match"))
        SwipeMatcherActor.doTheMatching(MatchCriteria.UNIVERSAL, request, existingRequests)
      } else {
        // we're working with pinch
        Logger.debug(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, "found a pinch match"))
        PinchMatcherActor.doTheMatching(MatchCriteria.UNIVERSAL, request, existingRequests)
      }
    }
  }
}
