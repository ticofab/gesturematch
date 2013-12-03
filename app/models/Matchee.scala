package models

import akka.actor.ActorRef
import consts.ScreenPositions.ScreenPosition
import consts.ScreenPositions
import play.api.libs.json.{JsObject, Json}
import consts.json.JsonResponseLabels

case class Matchee(handlingActor: ActorRef,
                   idInGroup: Int,
                   position: ScreenPosition = ScreenPositions.PositionUndetermined)

object Matchee {
  def toJson(matchee: Matchee): JsObject = {
    Json.obj(
      JsonResponseLabels.ID_IN_GROUP -> matchee.idInGroup,
      JsonResponseLabels.POSITION_IN_GROUP -> matchee.position.toString
    )
  }
}
