package helpers.json

import play.api.libs.json.Json
import consts.json.{JsonGeneralLabels, JsonMessageLabels}

object JsonMessageHelper {

  def createMatcheeSendsPayloadMessage(senderId: Int, payload: String) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
        JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_DELIVERY,
        JsonMessageLabels.MESSAGE_MATCHEE_ID -> senderId,
        JsonGeneralLabels.PAYLOAD -> payload
      )
    )
  }

  def createMatcheeLeavesMessage(senderId: Int) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
        JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_MATCHEE_LEFT_GROUP,
        JsonMessageLabels.MESSAGE_MATCHEE_ID -> senderId
      )
    )
  }
}
