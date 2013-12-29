package models

import akka.actor.ActorRef
import consts.SwipeMovements.SwipeMovement
import consts.Areas.Areas

class RequestToMatch(val apiKey: String,
                     val appId: String,
                     val deviceId: String,
                     val latitude: Double,
                     val longitude: Double,
                     val timestamp: Long,
                     val areaStart: Areas,
                     val areaEnd: Areas,
                     val movement: SwipeMovement,
                     val equalityParam: Option[String],
                     val orientation: Option[Double],
                     val swipeOrientation: Option[Double],
                     val handlingActor: ActorRef) {
  override def toString: String = s"Request: $apiKey $appId $deviceId $latitude $longitude $timestamp $areaStart $areaEnd $movement $equalityParam $handlingActor"
}
