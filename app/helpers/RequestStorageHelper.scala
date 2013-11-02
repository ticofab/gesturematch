package helpers

import models.RequestToMatch
import consts.Timeouts
import storage.RequestStorage
import play.api.Logger

// This object is an helper to access the storage of previously received requests.
object RequestStorageHelper {

  // true if the given request r is young enough
  private def oldRequestsFilter(r: RequestToMatch): Boolean =
    r.timestamp >= System.currentTimeMillis - Timeouts.maxOldestRequestIntervalMillis

  // true if the new request is not a "duplicate" within a too short interval of a previous one
  private def sameDeviceRequestFilter(rNew: RequestToMatch, rOld: RequestToMatch): Boolean =
    !(rNew.deviceId == rOld.deviceId && rNew.timestamp <= rOld.timestamp + Timeouts.maxOldestRequestIntervalMillis)

  def getValidExistingRequests(r: RequestToMatch): List[RequestToMatch] = {
    // filters the current requests, updates and returns
    RequestStorage.skimRequests(oldRequestsFilter)
    RequestStorage.skimRequests(sameDeviceRequestFilter, r)
    RequestStorage.getRequests
  }

  def storeNewRequest(newRequest: RequestToMatch) = {
    Logger.info("RequestStorageHelper, adding " + newRequest.toString)
    RequestStorage.addRequest(newRequest)
  }

  def removeRequests(requests: List[RequestToMatch]) = RequestStorage.skimRequests(r => !requests.contains(r))
}
