package models.ClientInputMessages

import play.api.libs.json.JsValue

case class ClientInputMessageDisconnect(reason: Option[String]) extends ClientInputMessage {

}

object ClientInputMessageDisconnect {
  def fromJson(jsonMsg: JsValue): ClientInputMessageDisconnect = {
    ClientInputMessageDisconnect(Some("fabio"))
  }
}
