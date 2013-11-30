package models

import akka.actor.ActorRef
import consts.ScreenPositions.ScreenPosition
import consts.{JsonLabels, ScreenPositions}
import play.api.libs.json.{JsObject, Json}

case class MatcheeInfo(handlingActor: ActorRef,
                       idInGroup: Int,
                       position: ScreenPosition = ScreenPositions.PositionUndetermined)

object MatcheeInfo {

  def getInfoObj(info: MatcheeInfo): JsObject = {
    Json.obj(
      JsonLabels.ID_IN_GROUP -> info.idInGroup,
      JsonLabels.POSITION_IN_GROUP -> info.position.toString
    )
  }
}
