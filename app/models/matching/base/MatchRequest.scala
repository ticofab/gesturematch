package models.matching.base

import akka.actor.ActorRef
import consts.Areas.Areas
import consts.SwipeMovements.SwipeMovement

abstract class MatchRequest(val deviceId: String,
                            val timestamp: Long,
                            val areaStart: Areas,
                            val areaEnd: Areas,
                            val movement: SwipeMovement,
                            val equalityParam: Option[String],
                            val handlingActor: ActorRef)