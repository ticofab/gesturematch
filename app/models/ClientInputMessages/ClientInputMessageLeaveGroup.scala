package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.{JsonGeneralLabels, JsonInputLabels}

case class ClientInputMessageLeaveGroup(reason: Option[String]) extends ClientInputMessage

object ClientInputMessageLeaveGroup {
  def fromJson(jsonValue: JsValue): ClientInputMessageLeaveGroup = {
    val reason = (jsonValue \ JsonGeneralLabels.REASON).asOpt[String]
    ClientInputMessageLeaveGroup(reason)
  }
}
