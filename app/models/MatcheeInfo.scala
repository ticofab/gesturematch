package models

import akka.actor.ActorRef
import consts.ScreenPositions.ScreenPosition
import consts.{JsonResponseLabels, ScreenPositions}
import play.api.libs.json.{JsObject, Json}

case class MatcheeInfo(handlingActor: ActorRef,
                       idInGroup: Int,
                       position: ScreenPosition = ScreenPositions.PositionUndetermined)

object MatcheeInfo {

  def getInfoObj(info: MatcheeInfo): JsObject = {
    Json.obj(
      JsonResponseLabels.ID_IN_GROUP -> info.idInGroup,
      JsonResponseLabels.POSITION_IN_GROUP -> info.position.toString
    )
  }
}
