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

package helpers.requests

import models.RequestToMatch
import consts.{Criteria, Timeouts}
import storage._
import play.api.Logger
import consts.Criteria.Criteria

// This object is an helper to access the storage of previously received requests.
object RequestStorageHelper {

  lazy val myName = RequestStorageHelper.getClass.getSimpleName

  // true if the given request r is young enough
  private def oldRequestsFilter(r: RequestToMatch): Boolean =
    r.timestamp >= System.currentTimeMillis - Timeouts.maxOldestRequestIntervalMillis

  // true if the new request is not a "duplicate" within a too short interval of a previous one
  private def sameDeviceRequestFilter(rNew: RequestToMatch, rOld: RequestToMatch): Boolean =
    !(rNew.deviceId == rOld.deviceId && rNew.timestamp <= rOld.timestamp + Timeouts.maxOldestRequestIntervalMillis)

  /**
   * This method provides the mandatory checks between two requests. Only put here the checks that are mandatory
   * for each request to be matched.
   *
   * @param r1
   * The first of the two requests to possibly match.
   * @param r2
   * The second of the two requests to possibly match.
   * @return
   * True or false whether the two requests might match.
   */
  private def compatibilityFilter(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if equality parameters are the same
    val equality =
      if (r1.equalityParam.isDefined && r2.equalityParam.isDefined) r1.equalityParam.get == r2.equalityParam.get
      else true

    closeEnough && equality
  }

  private def getCorrespondingStorage(criteria: Criteria): RequestStorage = {
    criteria match {
      case Criteria.SWIPE => SwipeRequestStorage
      case Criteria.PINCH => PinchRequestStorage
    }
  }

  def getValidExistingRequests(criteria: Criteria, r: RequestToMatch): List[RequestToMatch] = {
    // filters the current requests in the storage: remove the old ones and the ones from the same device
    val storage = getCorrespondingStorage(criteria)
    storage.skimRequests(oldRequestsFilter)
    storage.skimRequests(sameDeviceRequestFilter, r)

    // get the requests and apply mandatory filters
    storage.getRequests.filter(compatibilityFilter(_, r))
  }

  def storeNewRequest(criteria: Criteria, newRequest: RequestToMatch) = {
    Logger.info(s"$myName, adding new request to the $criteria storage")
    getCorrespondingStorage(criteria).addRequest(newRequest)
  }

  def removeRequests(criteria: Criteria, requests: List[RequestToMatch]) = {
    getCorrespondingStorage(criteria).skimRequests(r => !requests.contains(r))
  }
}
