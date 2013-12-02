package helpers

import play.api.libs.json.{JsValue, Json}
import models.ClientInputMessages.{ClientInputMessageBreakMatch, ClientInputMessageMatch, ClientInputMessageDisconnect, ClientInputMessage}
import consts.JsonInputLabels
import play.api.Logger

object JsonInputHelper {
  def parseInput(input: String): ClientInputMessage = {
    val jsonValue: JsValue = Json.parse(input)

    val inputType = (jsonValue \ JsonInputLabels.INPUT_TYPE).asOpt[String]

    inputType match {
      case Some(header) => {
        Logger.info(s"Input parsed, type $header")

        header match {
          case JsonInputLabels.INPUT_TYPE_MATCH => ClientInputMessageMatch.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_BREAK_MATCH => ClientInputMessageBreakMatch.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_DISCONNECT => ClientInputMessageDisconnect.fromJson(jsonValue)
          case JsonInputLabels.INPUT_TYPE_DELIVERY => ???
        }
      }
      case None => ??? // TODO: send bad message or something
    }
  }
}
