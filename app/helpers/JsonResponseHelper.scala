package helpers

import play.api.libs.json.Json
import consts.JsonLabels
import akka.actor.ActorRef

object JsonResponseHelper {

  def getMatched2ContentResponse(otherInfo: Vector[(ActorRef, String)]) = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED2,
        JsonLabels.PAYLOAD -> otherInfo.head._2
      )
    )
  }

  def getMatched4ContentResponse(otherInfo: Vector[(ActorRef, String)]) = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED4,
        JsonLabels.PAYLOAD -> Json.arr(
          Json.obj(
            JsonLabels.FIRST_DEVICE -> otherInfo(0)._2,
            JsonLabels.SECOND_DEVICE -> otherInfo(1)._2,
            JsonLabels.THIRD_DEVICE -> otherInfo(2)._2
          )
        )
      )
    )
  }

  def getTimeoutResponse = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_TIMEOUT
      )
    )
  }

  def getUnknownErrorResponse = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_UNKNOWN_ERROR
      )
    )
  }

}
