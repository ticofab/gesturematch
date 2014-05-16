package helpers.json

import consts.json.{JsonErrorLabels, JsonGeneralLabels}
import play.api.libs.json.Json

object JsonErrorHelper {
  def createInvalidCredentialsError(apiKey: String, appId: String) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonErrorLabels.KIND_ERROR,
        JsonGeneralLabels.TYPE -> JsonErrorLabels.TYPE_APIKEY_APP_ID_INVALID,
        JsonGeneralLabels.REASON -> s"${JsonErrorLabels.REASON_APIKEY_APPID_INVALID}: $apiKey, $appId"
      )
    )
  }

  def createDatabaseError = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonErrorLabels.KIND_ERROR,
        JsonGeneralLabels.TYPE -> JsonErrorLabels.TYPE_SERVER_ERROR,
        JsonGeneralLabels.REASON -> JsonErrorLabels.REASON_DATABASE_UNAVAILABLE
      )
    )
  }

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
