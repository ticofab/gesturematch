package helpers

import models.RequestToMatch
import consts.{Criteria, Timeouts}
import storage.{RequestStorage, TouchRequestStorage, PositionRequestStorage}
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

  private def getCorrespondingStorage(criteria: Criteria): RequestStorage = {
    criteria match {
      case Criteria.POSITION => PositionRequestStorage
      case Criteria.PRESENCE => TouchRequestStorage
    }
  }

  def getValidExistingRequests(criteria: Criteria, r: RequestToMatch): List[RequestToMatch] = {
    // filters the current requests, updates and returns
    val storage = getCorrespondingStorage(criteria)
    storage.skimRequests(oldRequestsFilter)
    storage.skimRequests(sameDeviceRequestFilter, r)
    storage.getRequests
  }

  def storeNewRequest(criteria: Criteria, newRequest: RequestToMatch) = {
    Logger.info(s"$myName, adding new request to the $criteria storage")
    getCorrespondingStorage(criteria).addRequest(newRequest)
  }

  def removeRequests(criteria: Criteria, requests: List[RequestToMatch]) = {
    getCorrespondingStorage(criteria).skimRequests(r => !requests.contains(r))
  }
}
