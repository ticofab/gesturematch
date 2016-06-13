/*
 * Copyright 2014-2016 Fabio Tiriticco, Fabway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package actors

import akka.actor.{Actor, Props}
import consts.MatchCriteria.MatchCriteria
import consts.{MatchCriteria, SwipeMovementType}
import helpers.logging.LoggingHelper
import helpers.matchers.pinch.PinchMatchingHelper
import helpers.matchers.swipe.SwipeMatchingHelper
import helpers.requests.RequestStorageHelper
import models.matching.base.MatchRequest
import models.matching.{GroupComMatchRequest, GroupCreateMatchRequest, Matchee}
import models.messages.actors.{Matched, MatchedInGroup, NewComRequest, NewCreateRequest}
import models.scheme.Scheme
import play.api.Logger
import traits.StringGenerator

class PinchMatcherActor extends Actor {

  def receive: Actor.Receive = {
    case NewComRequest(request) =>
      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingComRequests(MatchCriteria.PINCH, request)
      PinchMatcherActor.doTheMatching(MatchCriteria.PINCH, request, existingRequests)

    case NewCreateRequest(request) =>
      // obtain valid existing requests and filter by time
      val existingRequests = RequestStorageHelper.getValidExistingCreateRequests(MatchCriteria.PINCH, request)
      PinchMatcherActor.doTheMatching(MatchCriteria.PINCH, request, existingRequests)
  }
}

object PinchMatcherActor extends StringGenerator {
  val props = Props(classOf[PinchMatcherActor])

  lazy val myName = this.getClass.getSimpleName

  def doTheMatching(criteria: MatchCriteria, request: MatchRequest, existingRequests: List[MatchRequest]) = {
    // Try to create a match between the requests
    val matches: List[MatchRequest] = PinchMatchingHelper.getMatchingGroup(request, existingRequests)

    // This is where I know whether I
    //   1. haven't found anything
    //   2. have univocally identified a match
    //   3. have identified multiple matches, which is uncertainty.
    matches match {
      case Nil =>
        Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.length, "no group has been found."))

        // simply update the requests storage with the new request and the filtered requests
        RequestStorageHelper.storeNewRequest(criteria, request)

      case prevReq :: Nil =>

        // remove the group from the storage
        RequestStorageHelper.removeRequests(criteria, List(prevReq))

        request match {
          case gCom: GroupComMatchRequest =>
            val groupId = gCom.groupId.getOrElse("") // there should always be a value but just in case..
            Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.length, s"2 devices matched in group $groupId found"))

            // TODO: add scheme?
            request.handlingActor ! MatchedInGroup(MatchCriteria.PINCH, SwipeMovementType.OUTGOING, List(prevReq.asInstanceOf[GroupComMatchRequest].idInGroup.get))
            prevReq.handlingActor ! MatchedInGroup(MatchCriteria.PINCH, SwipeMovementType.OUTGOING, List(gCom.idInGroup.get))

          case gCre: GroupCreateMatchRequest =>
            // generate a unique groupId
            val groupId = getGroupUniqueString

            Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.length, s"match found: $groupId"))

            // Send a matching notification to the actors managing the corresponding devices
            val scheme: Scheme = new Scheme
            val id1: Int = scheme.addFirstDevice()
            val id2: Int = scheme.addDevice(SwipeMatchingHelper.getDeviceSchemePosition(prevReq.areaEnd), id1)
            val matchee1 = new Matchee(request.handlingActor, id1)
            val matchee2 = new Matchee(prevReq.handlingActor, id2)

            val message = new Matched(MatchCriteria.PINCH, SwipeMovementType.OUTGOING, List(matchee1, matchee2), groupId, Some(scheme))

            request.handlingActor ! message
            prevReq.handlingActor ! message
        }

      case group :: tail =>
        // TODO
        Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.length, s"${matches.size} groups found. uncertainty."))

    }
  }
}
