package helpers.json

import play.api.libs.json._
import models.Matchee
import consts.json.{JsonErrorLabels, JsonInputLabels, JsonGeneralLabels, JsonResponseLabels}
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.Some

object JsonResponseHelper {

  def createMatchedResponse(myInfo: Matchee, otherInfos: List[Matchee], groupId: String) = {

    val objList: List[JsObject] = otherInfos.map(info => Matchee.toJson(info))
    val jsonArray: JsValue = Json.toJson(objList)

    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
        JsonGeneralLabels.TYPE -> JsonInputLabels.INPUT_TYPE_MATCH,
        JsonGeneralLabels.OUTCOME -> JsonGeneralLabels.OK,
        JsonGeneralLabels.GROUP_ID -> JsString(groupId),
        JsonResponseLabels.MYSELF_IN_GROUP -> Matchee.toJson(myInfo),
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

  def getGroupLeftResponse(groupId: String) = getGroupOutcomeResponse(groupId,
    JsonInputLabels.INPUT_TYPE_LEAVE_GROUP, JsonGeneralLabels.OK)

  def getDisconnectResponse = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DISCONNECT, JsonGeneralLabels.OK)

  def getNotPartOfGroupResponse(groupId: String) = getGroupOutcomeResponse(groupId,
    JsonInputLabels.INPUT_TYPE_LEAVE_GROUP, JsonGeneralLabels.FAIL,
    Some(JsonResponseLabels.REASON_NOT_PART_OF_THIS_GROUP))

  def getNoGroupToLeaveResponse = getOutcomeResponse(
    JsonInputLabels.INPUT_TYPE_LEAVE_GROUP, JsonGeneralLabels.FAIL,
    Some(JsonResponseLabels.REASON_NOT_PART_OF_ANY_GROUP))

  def getPayloadPartiallyDeliveredResponse(groupId: String) = getGroupOutcomeResponse(groupId,
    JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.PARTIAL,
    Some(JsonResponseLabels.REASON_PAYLOAD_PARTIALLY_DELIVERED))

  def getPayloadNotDeliveredResponse(groupId: String, reason: Option[String] = None) = getGroupOutcomeResponse(groupId,
    JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.FAIL, reason)

  def getPayloadDeliveredResponse(groupId: String) = getGroupOutcomeResponse(groupId,
    JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.OK)

  def getPayloadEmptyGroupResponse(groupId: String) = getOutcomeResponse(JsonInputLabels.INPUT_TYPE_DELIVERY,
    JsonGeneralLabels.FAIL, Some(JsonResponseLabels.REASON_NOT_PART_OF_ANY_GROUP))

  lazy val notPartOfGroup = "You are not part of group "

  def getWrongGroupIdResponse(groupId: String) = {
    getInvalidInputResponse(Some(notPartOfGroup + groupId))
  }

  def getInvalidInputResponse(reason: Option[String] = None) = {
    val jsObj = Json.obj(
      JsonGeneralLabels.KIND -> JsonErrorLabels.KIND_ERROR,
      JsonGeneralLabels.TYPE -> JsonErrorLabels.TYPE_SERVER_ERROR
    )

    reason match {
      case Some(r) => Json.stringify(jsObj.+(JsonGeneralLabels.REASON, JsString(r)))
      case None => Json.stringify(jsObj)
    }
  }

  private def getGroupOutcomeResponse(groupId: String, `type`: String, outcome: String, reason: Option[String] = None) = {

    val jsObj = Json.obj(
      JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
      JsonGeneralLabels.TYPE -> `type`,
      JsonGeneralLabels.OUTCOME -> outcome,
      JsonGeneralLabels.GROUP_ID -> groupId
    )

    reason match {
      case Some(r) => Json.stringify(jsObj.+(JsonGeneralLabels.REASON, JsString(r)))
      case None => Json.stringify(jsObj)
    }
  }

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

  def getServerErrorResponse = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonErrorLabels.KIND_ERROR,
        JsonGeneralLabels.TYPE -> JsonErrorLabels.TYPE_SERVER_ERROR,
        JsonGeneralLabels.REASON -> JsonErrorLabels.REASON_SERVER_ERROR_NO_ACTOR
      )
    )
  }
}
