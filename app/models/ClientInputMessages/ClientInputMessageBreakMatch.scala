package models.ClientInputMessages

import play.api.libs.json.JsValue
import consts.json.JsonInputLabels

case class ClientInputMessageBreakMatch(reason: Option[String]) extends ClientInputMessage

object ClientInputMessageBreakMatch {
  def fromJson(jsonValue: JsValue): ClientInputMessageBreakMatch = {
    val reason = (jsonValue \ JsonInputLabels.INPUT_REASON).asOpt[String]
    ClientInputMessageBreakMatch(reason)
  }
}
