package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.JsonInputLabels

case class ClientInputMessageLeaveGroup(reason: Option[String]) extends ClientInputMessage

object ClientInputMessageLeaveGroup {
  def fromJson(jsonValue: JsValue): ClientInputMessageLeaveGroup = {
    val reason = (jsonValue \ JsonInputLabels.INPUT_REASON).asOpt[String]
    ClientInputMessageLeaveGroup(reason)
  }
}
