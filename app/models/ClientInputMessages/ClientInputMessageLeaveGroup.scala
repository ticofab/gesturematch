package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.{JsonGeneralLabels, JsonInputLabels}

case class ClientInputMessageLeaveGroup(groupId: String, reason: Option[String]) extends ClientInputMessage

object ClientInputMessageLeaveGroup {
  def fromJson(jsonValue: JsValue): ClientInputMessageLeaveGroup = {
    val groupId = (jsonValue \ JsonGeneralLabels.GROUP_ID).as[String]
    val reason = (jsonValue \ JsonGeneralLabels.REASON).asOpt[String]
    ClientInputMessageLeaveGroup(groupId, reason)
  }
}
