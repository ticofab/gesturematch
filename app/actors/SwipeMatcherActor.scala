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
import consts.Criteria
import helpers.matchers.swipe.PatternHelper
import helpers.requests.RequestStorageHelper
import helpers.storage.DBHelper
import models.matching.{Matchee, RequestToMatch}
import models.messages.actors.{Matched, NewRequest}
import models.scheme.Scheme
import play.api.Logger
import traits.StringGenerator

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


          val zippedMatches: List[(RequestToMatch, Int)] = matches.zipWithIndex

          val scheme: Option[Scheme] = if (isUnique) {
            def addZippedDevicesToScheme(devices: List[(RequestToMatch, Int)], scheme: Scheme): Unit = {
              if (devices.nonEmpty) {
                val (r, id) = devices.head
                val pos = PatternHelper.getDeviceSchemePosition(r.areaStart)
                scheme.addNextDevice(pos, id)
                addZippedDevicesToScheme(devices.tail, scheme)
              }
            }

            val scheme: Scheme = new Scheme()
            addZippedDevicesToScheme(zippedMatches, scheme)
            Some(scheme)
          } else None

          val matcheesInfo: List[Matchee] = zippedMatches.map(x => Matchee(x._1.handlingActor, x._2))
          matches.foreach(r => r.handlingActor ! Matched(matcheesInfo, groupId, scheme))

          DBHelper.addMatchEstablished(request.apiKey, request.appId, matches.length)
      }
  }
}

object SwipeMatcherActor {
  val props = Props(classOf[SwipeMatcherActor])
}
