package helpers.json

import play.api.libs.json.Json
import consts.json.{JsonGeneralLabels, JsonMessageLabels}

object JsonMessageHelper {

  def createMatcheeSendsPayloadMessage(groupId: String, senderId: Int, payload: String) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
        JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_DELIVERY,
        JsonGeneralLabels.GROUP_ID -> groupId,
        JsonMessageLabels.MESSAGE_MATCHEE_ID -> senderId,
        JsonGeneralLabels.PAYLOAD -> payload
      )
    )
  }

  def createMatcheeLeftGroupMessage(groupId: String, leaverId: Int) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
        JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_MATCHEE_LEFT_GROUP,
        JsonGeneralLabels.GROUP_ID -> groupId,
        JsonMessageLabels.MESSAGE_MATCHEE_ID -> leaverId
      )
    )
  }
}
