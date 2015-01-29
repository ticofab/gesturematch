/*
 * Copyright 2014 Fabio Tiriticco, Fabway
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
import consts.MatchCriteria
import consts.MatchCriteria.MatchCriteria
import helpers.logging.LoggingHelper
import helpers.matchers.swipe.SwipeMatchingHelper
import helpers.movements.SwipeMovementHelper
import helpers.requests.RequestStorageHelper
import models.matching.base.MatchRequest
import models.matching.{GroupComMatchRequest, GroupCreateMatchRequest, Matchee}
import models.messages.actors.{Matched, MatchedInGroup, NewComRequest, NewCreateRequest}
import models.scheme.Scheme
import play.api.Logger
import traits.StringGenerator

class SwipeMatcherActor extends Actor {

  def receive: Actor.Receive = {
    case NewComRequest(request) =>
      // get a list of possibly matching requests
      val existingRequests: List[MatchRequest] = RequestStorageHelper.getValidExistingComRequests(MatchCriteria.SWIPE, request)
      SwipeMatcherActor.doTheMatching(MatchCriteria.SWIPE, request, existingRequests)

    case NewCreateRequest(request) =>
      // get a list of possibly matching requests
      val existingRequests: List[MatchRequest] = RequestStorageHelper.getValidExistingCreateRequests(MatchCriteria.SWIPE, request)
      SwipeMatcherActor.doTheMatching(MatchCriteria.SWIPE, request, existingRequests)
  }
}

object SwipeMatcherActor extends StringGenerator {
  val props = Props(classOf[SwipeMatcherActor])

  lazy val myName = this.getClass.getSimpleName

  def doTheMatching(criteria: MatchCriteria, request: MatchRequest, existingRequests: List[MatchRequest]) = {
    // try to find a matching pattern
    val (matches, isUnique): (List[MatchRequest], Boolean) = SwipeMatchingHelper.getMatchedPattern(request :: existingRequests)

    matches match {
      case Nil =>
        // no match. add the request to the storage
        Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, "no match found."))
        RequestStorageHelper.storeNewRequest(criteria, request)

      case x :: Nil =>
        // only one element. wrong!
        Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, "error: group with one element."))

      case x :: xs =>
        // we identified a group!
        Logger.info(LoggingHelper.getNewRequestLogging(myName, request, existingRequests.size, s"group found, size: ${matches.size}"))
        RequestStorageHelper.removeRequests(criteria, matches)

        request match {
          case gCom: GroupComMatchRequest =>
            if (gCom.groupId.isDefined) {
              // this was a group request
              matches.foreach(requestBeingConsidered => {
                val matcheesId: List[Int] = matches.filter({
                  r => r.asInstanceOf[GroupComMatchRequest].idInGroup != requestBeingConsidered.asInstanceOf[GroupComMatchRequest].idInGroup
                }).map(r => r.asInstanceOf[GroupComMatchRequest].idInGroup).flatten
                val movementType = SwipeMovementHelper.swipeMovementToType(requestBeingConsidered.movement)
                requestBeingConsidered.handlingActor ! MatchedInGroup(MatchCriteria.SWIPE, movementType, matcheesId)
              })
            }

          case gCre: GroupCreateMatchRequest =>
            // get unique group id
            val groupId = getGroupUniqueString

            val zippedMatches: List[(MatchRequest, Int)] = matches.zipWithIndex

            val scheme: Option[Scheme] = if (isUnique) {
              def addZippedDevicesToScheme(devices: List[(MatchRequest, Int)], scheme: Scheme): Unit = {
                if (devices.nonEmpty) {
                  val (r, id) = devices.head
                  val pos = SwipeMatchingHelper.getDeviceSchemePosition(r.areaStart)
                  scheme.addNextDevice(pos, id)
                  addZippedDevicesToScheme(devices.tail, scheme)
                }
              }

              val scheme: Scheme = new Scheme()
              addZippedDevicesToScheme(zippedMatches, scheme)
              Some(scheme)
            } else None

            val matcheesInfo: List[Matchee] = zippedMatches.map(x => Matchee(x._1.handlingActor, x._2))
            matches.foreach(r => {
              val movementType = SwipeMovementHelper.swipeMovementToType(r.movement)
              r.handlingActor ! Matched(MatchCriteria.SWIPE, movementType, matcheesInfo, groupId, scheme)
            })
        }
    }
  }
}
