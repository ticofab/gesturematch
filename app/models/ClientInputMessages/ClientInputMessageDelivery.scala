package models.ClientInputMessages

import play.api.libs.json.{JsArray, JsValue}
import consts.json.JsonInputLabels

case class ClientInputMessageDelivery(recipients: List[Int], payload: String) extends ClientInputMessage

object ClientInputMessageDelivery {
  def fromJson(jsonValue: JsValue): ClientInputMessageDelivery = {
    val recipients = (jsonValue \ JsonInputLabels.INPUT_RECIPIENTS).as[List[Int]]
    val payload: String = (jsonValue \ JsonInputLabels.INPUT_PAYLOAD).as[String]
    ClientInputMessageDelivery(recipients, payload)
  }
}


