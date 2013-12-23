package helpers.requests

import models.RequestToMatch
import consts.{Criteria, Timeouts}
import storage.{PinchRequestStorage, RequestStorage, PresenceRequestStorage, PositionRequestStorage}
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

  private def compatibilityFilter(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if equality parameters are the same
    val equality =
      if (r1.equalityParam.isDefined && r2.equalityParam.isDefined) r1.equalityParam.get == r2.equalityParam.get
      else true

    // check if apiKey is the same
    val sameApiKey = r1.apiKey == r2.apiKey

    // check if appId is the same
    val sameAppId = r1.appId == r2.appId

    closeEnough && equality && sameApiKey && sameAppId
  }

  private def getCorrespondingStorage(criteria: Criteria): RequestStorage = {
    criteria match {
      case Criteria.POSITION => PositionRequestStorage
      case Criteria.PRESENCE => PresenceRequestStorage
      case Criteria.PINCH => PinchRequestStorage
    }
  }

  def getValidExistingRequests(criteria: Criteria, r: RequestToMatch): List[RequestToMatch] = {
    // filters the current requests in the storage: remove the old ones and the ones from the same device
    val storage = getCorrespondingStorage(criteria)
    storage.skimRequests(oldRequestsFilter)
    storage.skimRequests(sameDeviceRequestFilter, r)

    // TODO: for different criteria, I could allow different timeouts or apply different filters
    // get the requests and apply subsequent filters as needed
    val requests = storage.getRequests
    requests.filter(compatibilityFilter(_, r))
  }

  def storeNewRequest(criteria: Criteria, newRequest: RequestToMatch) = {
    Logger.info(s"$myName, adding new request to the $criteria storage")
    getCorrespondingStorage(criteria).addRequest(newRequest)
  }

  def removeRequests(criteria: Criteria, requests: List[RequestToMatch]) = {
    getCorrespondingStorage(criteria).skimRequests(r => !requests.contains(r))
  }
}
