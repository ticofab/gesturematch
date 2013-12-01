package models.ClientInputMessages

import play.api.libs.json.JsValue

case class BreakMatchInputMessage(reason: Option[String]) {

}

object BreakMatchInputMessage {
  def fromJson(jsonValue: JsValue): BreakMatchInputMessage = {

    // TODO
    BreakMatchInputMessage(None)
  }
}
