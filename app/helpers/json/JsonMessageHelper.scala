package helpers.json

import play.api.libs.json.{JsNumber, Json}
import consts.json.{JsonInputLabels, JsonGeneralLabels, JsonMessageLabels}
import models.Delivery

object JsonMessageHelper {

  def createMatcheeSendsPayloadMessage(groupId: String, senderId: Int, delivery: Delivery) = {
    var json = Json.obj(
      JsonGeneralLabels.KIND -> JsonMessageLabels.KIND_MESSAGE,
      JsonGeneralLabels.TYPE -> JsonMessageLabels.MESSAGE_TYPE_DELIVERY,
      JsonGeneralLabels.GROUP_ID -> groupId,
      JsonMessageLabels.MESSAGE_MATCHEE_ID -> senderId,
      JsonInputLabels.INPUT_DELIVERY_ID -> delivery.id,
      JsonGeneralLabels.PAYLOAD -> delivery.payload
    )

    if (delivery.chunk.isDefined) json = json + (JsonInputLabels.INPUT_CHUNK_NUMBER, JsNumber(delivery.chunk.get))
    if (delivery.totalChunks.isDefined) json = json + (JsonInputLabels.INPUT_TOTAL_CHUNKS, JsNumber(delivery.totalChunks.get))

    Json.stringify(json)
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
