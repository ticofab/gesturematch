package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.{JsonGeneralLabels, JsonInputLabels}

case class ClientInputMessageDelivery(groupId: String,
                                      recipients: Option[List[Int]],
                                      deliveryId: String,
                                      payload: String,
                                      chunkNr: Option[Int],
                                      totalChunks: Option[Int]) extends ClientInputMessage

object ClientInputMessageDelivery {
  def fromJson(jsonValue: JsValue): ClientInputMessageDelivery = {
    // group stuff
    val groupId = (jsonValue \ JsonGeneralLabels.GROUP_ID).as[String]

    // delivery stuff
    val recipients: Option[List[Int]] = (jsonValue \ JsonInputLabels.INPUT_RECIPIENTS).asOpt[List[Int]]
    val payload: String = (jsonValue \ JsonGeneralLabels.PAYLOAD).as[String]
    val deliveryId: String = (jsonValue \ JsonInputLabels.INPUT_DELIVERY_ID).as[String]

    // partial delivery stuff
    val chunk: Option[Int] = (jsonValue \ JsonInputLabels.INPUT_CHUNK_NUMBER).asOpt[Int]
    val totalChunks: Option[Int] = (jsonValue \ JsonInputLabels.INPUT_TOTAL_CHUNKS).asOpt[Int]

    ClientInputMessageDelivery(groupId, recipients, deliveryId, payload, chunk, totalChunks)
  }
}


