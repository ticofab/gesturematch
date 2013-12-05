package helpers.json

import play.api.libs.json._
import models.Matchee
import consts.json.{JsonInputLabels, JsonGeneralLabels, JsonResponseLabels}
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.Some
import play.api.libs.json.JsNumber

object JsonResponseHelper {

  def createMatchedResponse(myInfo: Matchee, otherInfos: List[Matchee]) = {

    val objList: List[JsObject] = otherInfos.map(info => Matchee.toJson(info))
    val jsonArray: JsValue = Json.toJson(objList)

    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
        JsonGeneralLabels.TYPE -> JsonInputLabels.INPUT_TYPE_MATCH,
        JsonGeneralLabels.OUTCOME -> JsonGeneralLabels.OK,
        JsonResponseLabels.GROUP_SIZE -> JsNumber(otherInfos.size + 1),
        JsonResponseLabels.ME_IN_GROUP -> Matchee.toJson(myInfo),
        JsonResponseLabels.OTHERS_IN_GROUP -> jsonArray
      )
    )
  }

  def getInvalidMatchRequestResponse = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
        JsonGeneralLabels.TYPE -> JsonInputLabels.INPUT_TYPE_MATCH,
        JsonGeneralLabels.OUTCOME -> JsonGeneralLabels.FAIL,
        JsonGeneralLabels.REASON -> JsonResponseLabels.REASON_INVALID_REQUEST
      )
    )
  }

  def getTimeoutResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_MATCH, JsonGeneralLabels.FAIL,
    Some(JsonResponseLabels.REASON_TIMEOUT))

  def getMatchBrokenResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_LEAVE_GROUP, JsonGeneralLabels.OK)

  def getDisconnectResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DISCONNECT, JsonGeneralLabels.OK)

  def getNoGroupToLeaveResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_LEAVE_GROUP,
    JsonGeneralLabels.FAIL, Some(JsonResponseLabels.REASON_NOT_PART_OF_ANY_GROUP))

  def getPayloadPartiallyDeliveredResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DELIVERY,
    JsonGeneralLabels.PARTIAL, Some(JsonResponseLabels.REASON_PAYLOAD_PARTIALLY_DELIVERED))

  def getPayloadNotDeliveredResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.FAIL)

  def getPayloadDeliveredResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.OK)

  def getPayloadEmptyGroupResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.FAIL,
    Some(JsonResponseLabels.REASON_NOT_PART_OF_ANY_GROUP))

  private def getOutcomeResponse(`type`: String, outcome: String, reason: Option[String] = None) = {

    val jsObj = Json.obj(
      JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
      JsonGeneralLabels.TYPE -> `type`,
      JsonGeneralLabels.OUTCOME -> outcome
    )

    reason match {
      case Some(r) => Json.stringify(jsObj.+(JsonGeneralLabels.REASON, JsString(r)))
      case None => Json.stringify(jsObj)
    }
  }

  def getInvalidInputResponse = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_INVALID_INPUT
      )
    )
  }
}
