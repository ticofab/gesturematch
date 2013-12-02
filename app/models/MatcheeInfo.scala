package models

import akka.actor.ActorRef
import consts.ScreenPositions.ScreenPosition
import consts.{ScreenPositions}
import play.api.libs.json.{JsObject, Json}
import consts.json.JsonResponseLabels

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
