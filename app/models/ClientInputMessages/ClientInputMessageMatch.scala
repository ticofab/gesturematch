package models.ClientInputMessages

import play.api.libs.json.JsValue

// TODO
case class ClientInputMessageMatch() extends ClientInputMessage {

}

object ClientInputMessageMatch {
  def fromJson(jsonValue: JsValue): ClientInputMessageMatch = {
    ClientInputMessageMatch()
  }
}
