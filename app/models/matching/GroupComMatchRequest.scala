package models.matching

import akka.actor.ActorRef
import consts.Areas.Areas
import consts.SwipeMovements.SwipeMovement
import models.matching.base.MatchRequest

case class GroupComMatchRequest(override val deviceId: String,
                                override val timestamp: Long,
                                override val areaStart: Areas,
                                override val areaEnd: Areas,
                                override val movement: SwipeMovement,
                                override val equalityParam: Option[String],
                                override val handlingActor: ActorRef,
                                groupId: Option[String],
                                idInGroup: Option[Int])
  extends MatchRequest(deviceId, timestamp, areaStart, areaEnd, movement, equalityParam, handlingActor)
