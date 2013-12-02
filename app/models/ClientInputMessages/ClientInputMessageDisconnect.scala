package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.JsonInputLabels

case class ClientInputMessageDisconnect(reason: Option[String]) extends ClientInputMessage

object ClientInputMessageDisconnect {
  def fromJson(jsonValue: JsValue): ClientInputMessageDisconnect = {
    val reason = (jsonValue \ JsonInputLabels.INPUT_REASON).asOpt[String]
    ClientInputMessageDisconnect(reason)
  }
}
