package helpers

import models.RequestToMatch

// This object is an abstraction of the memory of previously received requests.
object RequestListHelper {

  // TODO: this could become a memcache DB or some other abstraction
  var currentRequests: List[RequestToMatch] = List()

  def getExistingRequests: List[RequestToMatch] = currentRequests

  def updateCurrentRequests(requests: List[RequestToMatch]): Unit = currentRequests = requests
}
