package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.JsonGeneralLabels

case class ClientInputMessageDisconnect(groupId: String, reason: Option[String]) extends ClientInputMessage

object ClientInputMessageDisconnect {
  def fromJson(jsonValue: JsValue): ClientInputMessageDisconnect = {
    val groupId = (jsonValue \ JsonGeneralLabels.GROUP_ID).as[String]
    val reason = (jsonValue \ JsonGeneralLabels.REASON).asOpt[String]
    ClientInputMessageDisconnect(groupId, reason)
  }
}
