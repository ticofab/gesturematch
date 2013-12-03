package models.ClientInputMessages

import play.api.libs.json.{JsArray, Json, JsValue}
import consts.json.JsonInputLabels

case class ClientInputMessageDelivery(recipients: List[Int], payload: String) extends ClientInputMessage


object ClientInputMessageDelivery {
  def fromJson(jsonValue: JsValue): ClientInputMessageDelivery = {
    val recipientsJsonArray = (jsonValue \ JsonInputLabels.INPUT_RECIPIENTS).as[JsArray]
    val recipientsJsValues = (recipientsJsonArray \\ JsonInputLabels.INPUT_RECIPIENT).toList
    val recipients = recipientsJsValues.map(x => x.as[Int])
    val payload: String = (jsonValue \ JsonInputLabels.INPUT_PAYLOAD).as[String]
    ClientInputMessageDelivery(recipients, payload)
  }
}


