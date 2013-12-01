package helpers

import play.api.libs.json.{JsValue, Json}
import models.ClientInputMessages.{ClientInputMessageBreakMatch, ClientInputMessageMatch, ClientInputMessageDisconnect, ClientInputMessage}
import consts.JsonLabels

object JsonInputHelper {
  def parseInput(input: String): ClientInputMessage = {
    val jsonValue: JsValue = Json.parse(input)

    val inputType = (jsonValue \ JsonLabels.INPUT_TYPE_MATCH).asOpt[String]
    inputType match {
      case Some(header) => {
        header match {
          case JsonLabels.INPUT_TYPE_MATCH => ClientInputMessageMatch.fromJson(jsonValue)
          case JsonLabels.INPUT_TYPE_BREAK_MATCH => ClientInputMessageBreakMatch.fromJson(jsonValue)
          case JsonLabels.INPUT_TYPE_DISCONNECT => ClientInputMessageDisconnect.fromJson(jsonValue)
          case JsonLabels.INPUT_TYPE_DELIVERY => ???
        }
      }
      case None => ??? // TODO: send bad message or something
    }
  }
}
