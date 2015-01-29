package helpers.json

import consts.json.{JsonErrorLabels, JsonGeneralLabels}
import play.api.libs.json.Json

object JsonErrorHelper {
  def createServerError = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonErrorLabels.KIND_ERROR,
        JsonGeneralLabels.TYPE -> JsonErrorLabels.TYPE_SERVER_ERROR,
        JsonGeneralLabels.REASON -> JsonErrorLabels.REASON_SERVER_ERROR_NO_ACTOR
      )
    )
  }
}
