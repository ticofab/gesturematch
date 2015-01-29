package storage.base

/**
 * implementation of Request Storage using a simple List
 */
class ListRequestStorage[A] extends RequestStorage[A] {
  // This could become a mem cache DB or some other abstraction
  var requests: List[A] = List[A]()

  // interfaces with the storage
  def getRequests = requests

  def addRequest(r: A) = requests = r :: requests

  def skimRequests(skimFilter: (A) => Boolean) = requests = requests.filter(skimFilter)

  def skimRequests(skimFilter: (A, A) => Boolean, r: A) = requests = requests.filter(skimFilter(r, _))
}
