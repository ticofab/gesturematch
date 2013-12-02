package helpers

import play.api.libs.json.{JsObject, Json}
import consts.JsonResponseLabels
import models.MatcheeInfo

object JsonResponseHelper {

  def createMatchedResponse(myInfo: MatcheeInfo, otherInfos: List[MatcheeInfo]) = {

    val objList: List[JsObject] = otherInfos.map(info => MatcheeInfo.getInfoObj(info))
    val jsonArray = Json.toJson(objList)

    Json.stringify(
      Json.obj(
        JsonResponseLabels.OUTCOME -> JsonResponseLabels.OUTCOME_MATCHED,
        JsonResponseLabels.GROUP_SIZE -> (otherInfos.size + 1),
        JsonResponseLabels.MY_CONNECTION_INFO -> MatcheeInfo.getInfoObj(myInfo),
        JsonResponseLabels.OTHERS_CONNECTION_INFO -> jsonArray
      )
    )
  }

  def getInvalidMatchRequestResponse = {
    Json.stringify(
      Json.obj(
        JsonResponseLabels.OUTCOME -> JsonResponseLabels.OUTCOME_INVALID_REQUEST
        // TODO: add reason
      )
    )
  }

  def getUnknownErrorResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_UNKNOWN_ERROR)
  def getTimeoutResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_TIMEOUT)
  def getMatchBrokenResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_MATCH_BROKEN)
  def getDisconnectResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_DISCONNECTED)
  def getInvalidInputResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_INPUT_INVALID)

  private def getSimpleOutcomeResponse(outcomeResponse: String) = {
    Json.stringify(
      Json.obj(
        JsonResponseLabels.OUTCOME -> outcomeResponse
      )
    )
  }

}
