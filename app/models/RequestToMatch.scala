package models

import akka.actor.ActorRef

/**
 * This is the object that represents a matching request that came from a client.
 * @param latitude         The client's latitude (to best approximation)
 * @param longitude        The client's longitude (to best approximation)
 * @param timestamp        The timestamp at the request arrival
 * @param swipeStart       The area where the swipe movement started
 * @param swipeEnd         The area where the swipe movement ended
 * @param equalityParam    The equality param (an arbitrary parameter)
 * @param payload          The request payload
 * @param handlingActor    The reference to the actor managing this request
 */
class RequestToMatch(val latitude: Double,
                     val longitude: Double,
                     val timestamp: Long,
                     val swipeStart: Int,
                     val swipeEnd: Int,
                     val equalityParam: String,
                     val payload: String,
                     val handlingActor: ActorRef) {
  def getServiceInfo: (ActorRef, String) = (handlingActor, payload)
  override def toString: String = s"Request: $latitude $longitude $timestamp $swipeStart$swipeEnd $equalityParam $handlingActor\n    $payload"
}
