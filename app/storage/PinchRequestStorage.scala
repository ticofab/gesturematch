package storage

import models.RequestToMatch

object PinchRequestStorage extends RequestStorage {

  // This could become a memcache DB or some other abstraction
  private var requests: List[RequestToMatch] = List()

  // interfaces with the storage
  def getRequests = requests

  def addRequest(r: RequestToMatch) = requests = r :: requests

  def skimRequests(skimFilter: RequestToMatch => Boolean) = requests = requests.filter(skimFilter)

  def skimRequests(skimFilter: (RequestToMatch, RequestToMatch) => Boolean, r: RequestToMatch) =
    requests = requests.filter(skimFilter(r, _))
}
