package helpers.json

import play.api.libs.json.{JsObject, Json}
import models.Matchee
import consts.json.JsonResponseLabels

object JsonResponseHelper {

  def createMatchedResponse(myInfo: Matchee, otherInfos: List[Matchee]) = {

    val objList: List[JsObject] = otherInfos.map(info => Matchee.toJson(info))
    val jsonArray = Json.toJson(objList)

    Json.stringify(
      Json.obj(
        JsonResponseLabels.OUTCOME -> JsonResponseLabels.OUTCOME_MATCHED,
        JsonResponseLabels.GROUP_SIZE -> (otherInfos.size + 1),
        JsonResponseLabels.ME_IN_GROUP -> Matchee.toJson(myInfo),
        JsonResponseLabels.OTHERS_IN_GROUP -> jsonArray
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
  def getMatchBrokenResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_GROUP_LEFT)
  def getDisconnectResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_DISCONNECTED)
  def getInvalidInputResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_INPUT_INVALID)
  def getNothingToBreakResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_NO_GROUP_TO_LEAVE)
  def getPayloadPartiallyDeliveredResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_PAYLOAD_PARTIALLY_DELIVERED)
  def getPayloadNotDeliveredResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_PAYLOAD_NOT_DELIVERED)
  def getPayloadDeliveredResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_PAYLOAD_DELIVERED)
  def getPayloadEmptyGroupdResponse = getSimpleOutcomeResponse(JsonResponseLabels.OUTCOME_PAYLOAD_EMPTY_GROUP)

  private def getSimpleOutcomeResponse(outcomeResponse: String) = {
    Json.stringify(
      Json.obj(
        JsonResponseLabels.OUTCOME -> outcomeResponse
      )
    )
  }

}
