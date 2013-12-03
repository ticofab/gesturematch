package helpers.json

import play.api.libs.json.Json
import consts.json.JsonMessageLabels

object JsonMessageHelper {

  def createMatcheeSendsPayloadMessage(senderId: Int, payload: String) = {
    Json.stringify(
      Json.obj(
        JsonMessageLabels.MESSAGE_TYPE -> JsonMessageLabels.MESSAGE_TYPE_DELIVERY,
        JsonMessageLabels.MATCHEE_ID -> senderId,
        JsonMessageLabels.PAYLOAD -> payload
      )
    )
  }
  
  def createMatcheeLeavesMessage(senderId: Int) = {
    Json.stringify(
      Json.obj(
        JsonMessageLabels.MESSAGE_TYPE -> JsonMessageLabels.MESSAGE_TYPE_MATCHEE_LEFT_GROUP,
        JsonMessageLabels.MATCHEE_ID -> senderId
      )
    )
  }
}
