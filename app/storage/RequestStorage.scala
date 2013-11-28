package storage

import models.RequestToMatch

abstract class RequestStorage {

  // interfaces with the storage
  def getRequests: List[RequestToMatch]

  def addRequest(r: RequestToMatch): Unit

  def skimRequests(skimFilter: RequestToMatch => Boolean)

  def skimRequests(skimFilter: (RequestToMatch, RequestToMatch) => Boolean, r: RequestToMatch)
}
