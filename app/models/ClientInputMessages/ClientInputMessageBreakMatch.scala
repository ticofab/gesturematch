package models.ClientInputMessages

import play.api.libs.json.JsValue

case class ClientInputMessageBreakMatch(reason: Option[String]) extends ClientInputMessage

object ClientInputMessageBreakMatch {
  def fromJson(jsonValue: JsValue): ClientInputMessageBreakMatch = {

    // TODO
    ClientInputMessageBreakMatch(None)
  }
}
