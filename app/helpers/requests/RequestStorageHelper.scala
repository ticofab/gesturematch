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

package helpers.requests

import consts.MatchCriteria.MatchCriteria
import consts.{MatchCriteria, MatchingDistance, Timeouts}
import models.matching.base.MatchRequest
import models.matching.{GroupComMatchRequest, GroupCreateMatchRequest}
import play.api.Logger
import storage.groupcommunication.{PinchGroupComRequestStorage, SwipeGroupComRequestStorage, UniversalGroupComRequestStorage}
import storage.groupcreation.{PinchGroupCreateRequestStorage, SwipeGroupCreateRequestStorage, UniversalGroupCreateRequestStorage}

import scala.math._

// This object is an helper to access the storage of previously received requests.
object RequestStorageHelper {

  lazy val myName = RequestStorageHelper.getClass.getSimpleName

  // true if the given request r is young enough
  private def oldRequestsFilter(r: MatchRequest) =
    r.timestamp >= System.currentTimeMillis - Timeouts.maxOldestRequestIntervalMillis

  // true if the new request is not a "duplicate" within a too short interval of a previous one
  private def sameDeviceRequestFilter(rNew: MatchRequest, rOld: MatchRequest) =
    !(rNew.deviceId == rOld.deviceId && rNew.timestamp <= rOld.timestamp + Timeouts.maxOldestRequestIntervalMillis)

  // true if two requests both have a group and those groups are the same
  private def sameGroupRequestFilter(rNew: GroupComMatchRequest, rOld: GroupComMatchRequest) =
    rNew.groupId.isDefined && rOld.groupId.isDefined && rNew.groupId.getOrElse("") == rOld.groupId.getOrElse("")

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
  private def compatibilityFilter(r1: MatchRequest, r2: MatchRequest) = {
    // check if equality parameters are the same
    if (r1.equalityParam.isDefined && r2.equalityParam.isDefined) r1.equalityParam.get == r2.equalityParam.get
    else true
  }

  private def locationCompatibilityFilter(r1: GroupCreateMatchRequest, r2: GroupCreateMatchRequest) = {
    def haversineDistanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {
      val R = 6372.8 // radius in km

      val dLat = (lat2 - lat1).toRadians
      val dLon = (lon2 - lon1).toRadians

      val a = pow(sin(dLat / 2), 2) + pow(sin(dLon / 2), 2) * cos(lat1.toRadians) * cos(lat2.toRadians)
      val c = 2 * asin(sqrt(a))
      R * c
    }

    // check on location
    val distanceInMeters: Double = haversineDistanceInKm(r1.latitude, r1.longitude, r2.latitude, r2.longitude) * 1000
    Logger.debug(s"distance in meters: $distanceInMeters")

    distanceInMeters < MatchingDistance.closeEnoughDistance
  }

  private def getComStorage(criteria: MatchCriteria) = {
    criteria match {
      case MatchCriteria.SWIPE => SwipeGroupComRequestStorage.get
      case MatchCriteria.PINCH => PinchGroupComRequestStorage.get
      case MatchCriteria.UNIVERSAL => UniversalGroupComRequestStorage.get
    }
  }

  private def getCreateStorage(criteria: MatchCriteria) = {
    criteria match {
      case MatchCriteria.SWIPE => SwipeGroupCreateRequestStorage.get
      case MatchCriteria.PINCH => PinchGroupCreateRequestStorage.get
      case MatchCriteria.UNIVERSAL => UniversalGroupCreateRequestStorage.get
    }
  }

  def getValidExistingComRequests(criteria: MatchCriteria, r: GroupComMatchRequest): List[GroupComMatchRequest] = {
    val storage = getComStorage(criteria)
    storage.skimRequests(oldRequestsFilter)
    storage.skimRequests(sameDeviceRequestFilter, r)
    storage.getRequests.filter(compatibilityFilter(_, r)).filter(sameGroupRequestFilter(_, r))
  }

  def getValidExistingCreateRequests(criteria: MatchCriteria, r: GroupCreateMatchRequest): List[GroupCreateMatchRequest] = {
    val storage = getCreateStorage(criteria)
    storage.skimRequests(oldRequestsFilter)
    storage.skimRequests(sameDeviceRequestFilter, r)
    storage.getRequests.filter(compatibilityFilter(_, r)).filter(locationCompatibilityFilter(_, r))
  }

  def storeNewRequest(criteria: MatchCriteria, newRequest: MatchRequest) = {
    newRequest match {
      case gComR: GroupComMatchRequest =>
        getComStorage(criteria).addRequest(gComR)
        Logger.debug(s"$myName, adding new request to the COM $criteria storage")
      case gCreR: GroupCreateMatchRequest =>
        getCreateStorage(criteria).addRequest(gCreR)
        Logger.debug(s"$myName, adding new request to the CREATE $criteria storage")
    }
  }

  def removeRequests(criteria: MatchCriteria, requests: List[MatchRequest]) = {
    requests match {
      case head :: tail =>
        head match {
          case gCom: GroupComMatchRequest => getComStorage(criteria).skimRequests(r => !requests.contains(r))
          case gCre: GroupCreateMatchRequest => getCreateStorage(criteria).skimRequests(r => !requests.contains(r))
        }
      case _ => // TODO: error; nothing to remove?
    }
  }
}
