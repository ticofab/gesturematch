package models.ClientInputMessages

import play.api.libs.json.{JsArray, JsValue}
import consts.json.{JsonGeneralLabels, JsonInputLabels}

case class ClientInputMessageDelivery(groupId: String, recipients: List[Int], payload: String) extends ClientInputMessage

object ClientInputMessageDelivery {
  def fromJson(jsonValue: JsValue): ClientInputMessageDelivery = {
    val groupId = (jsonValue \ JsonGeneralLabels.GROUP_ID).as[String]
    val recipients = (jsonValue \ JsonInputLabels.INPUT_RECIPIENTS).as[List[Int]]
    val payload: String = (jsonValue \ JsonGeneralLabels.PAYLOAD).as[String]
    ClientInputMessageDelivery(groupId, recipients, payload)
  }
}


