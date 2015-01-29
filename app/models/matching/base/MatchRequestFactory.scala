package models.matching.base

import akka.actor.ActorRef
import consts.Areas.Areas
import consts.SwipeMovements.SwipeMovement
import models.matching.{GroupComMatchRequest, GroupCreateMatchRequest}
import models.messages.client.base.ClientInputMatchRequest
import models.messages.client.{ClientInputMsgGroupComMatch, ClientInputMsgGroupCreateMatch}

object MatchRequestFactory {
  def getMatchRequest(deviceId: String,
                      timestamp: Long,
                      areaStart: Areas,
                      areaEnd: Areas,
                      movement: SwipeMovement,
                      handlingActor: ActorRef,
                      matchRequestMsg: ClientInputMatchRequest): MatchRequest = {

    matchRequestMsg match {

      case groupCreate: ClientInputMsgGroupCreateMatch =>
        GroupCreateMatchRequest(deviceId, timestamp, areaStart, areaEnd, movement, groupCreate.equalityParam,
          handlingActor, groupCreate.latitude, groupCreate.longitude)

      case groupCom: ClientInputMsgGroupComMatch =>
        GroupComMatchRequest(deviceId, timestamp, areaStart, areaEnd, movement, groupCom.equalityParam,
          handlingActor, groupCom.groupId, groupCom.idInGroup)

    }
  }
}
