package helpers

import play.api.libs.json.{JsValue, Json}
import models.ClientInputMessages.{ClientInputMessageDisconnect, ClientInputMessage}

object JsonInputHelper {
  def parseInput(input: String): ClientInputMessage = {
    val jsonValue: JsValue = Json.parse(input)

    // TODO: choose the appropriate model
    ClientInputMessageDisconnect.fromJson(jsonValue)

  }
}
