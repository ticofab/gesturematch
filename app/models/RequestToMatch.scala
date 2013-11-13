package models

import akka.actor.ActorRef
import consts.SwipeMovements.SwipeMovement

/**
 * This is the object that represents a matching request that came from a client.
 * @param latitude         The client's latitude (to best approximation)
 * @param longitude        The client's longitude (to best approximation)
 * @param timestamp        The timestamp at the request arrival
 * @param movement         The swipe movement
 * @param equalityParam    The equality param (an arbitrary parameter)
 * @param payload          The request payload
 * @param handlingActor    The reference to the actor managing this request
 */
class RequestToMatch(val apiKey: String,
                     val deviceId: String,
                     val latitude: Double,
                     val longitude: Double,
                     val timestamp: Long,
                     val areaStart: Int,
                     val areaEnd: Int,
                     val movement: SwipeMovement,
                     val equalityParam: String,
                     val payload: String,
                     val handlingActor: ActorRef) {
  def getServiceInfo: (ActorRef, String) = (handlingActor, payload)

  override def toString: String = s"Request: $apiKey $deviceId $latitude $longitude $timestamp $areaStart$areaEnd $movement $equalityParam $handlingActor $payload"
}
