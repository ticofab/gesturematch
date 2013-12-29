package helpers.json

import play.api.libs.json._
import models.Matchee
import consts.json.{JsonErrorLabels, JsonInputLabels, JsonGeneralLabels, JsonResponseLabels}
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import scala.Some

object JsonResponseHelper {

  def createMatchedResponse(myself: Matchee, otherMatchees: List[Matchee], groupId: String) = {

    val objList: List[JsObject] = otherMatchees.map(info => Matchee.toJson(info))
    val jsonArray: JsValue = Json.toJson(objList)

    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
        JsonGeneralLabels.TYPE -> JsonInputLabels.INPUT_TYPE_MATCH,
        JsonGeneralLabels.OUTCOME -> JsonGeneralLabels.OK,
        JsonGeneralLabels.GROUP_ID -> JsString(groupId),
        JsonResponseLabels.MYSELF_IN_GROUP -> Matchee.toJson(myself),
        JsonResponseLabels.OTHERS_IN_GROUP -> jsonArray
      )
    )
  }

  def getInvalidMatchRequestResponse(msg: String) = {
    Json.stringify(
      Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
        JsonGeneralLabels.TYPE -> JsonInputLabels.INPUT_TYPE_MATCH,
        JsonGeneralLabels.OUTCOME -> JsonGeneralLabels.FAIL,
        JsonGeneralLabels.REASON -> JsonResponseLabels.REASON_INVALID_REQUEST,
        JsonGeneralLabels.DETAILS -> msg
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

  def getPayloadNotDeliveredResponse(groupId: String, reason: Option[String] = None) = getGroupOutcomeResponse(groupId,
    JsonInputLabels.INPUT_TYPE_DELIVERY, JsonGeneralLabels.FAIL, reason)

  lazy val notPartOfGroup = "You are not part of group "

  def getWrongGroupIdResponse(groupId: String) = {
    getInvalidInputResponse(Some(notPartOfGroup + groupId))
  }

  def getInvalidInputResponse(reason: Option[String] = None) = {
    var jsObj = Json.obj(
      JsonGeneralLabels.KIND -> JsonErrorLabels.KIND_ERROR,
      JsonGeneralLabels.TYPE -> JsonErrorLabels.TYPE_SERVER_ERROR
    )

    if (reason.isDefined) jsObj = jsObj +(JsonGeneralLabels.REASON, JsString(reason.get))

    Json.stringify(jsObj)
  }

  private def getGroupOutcomeResponse(groupId: String, `type`: String, outcome: String, reason: Option[String] = None) = {

    var jsObj = Json.obj(
      JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
      JsonGeneralLabels.TYPE -> `type`,
      JsonGeneralLabels.OUTCOME -> outcome,
      JsonGeneralLabels.GROUP_ID -> groupId
    )

    if (reason.isDefined) jsObj = jsObj +(JsonGeneralLabels.REASON, JsString(reason.get))

    Json.stringify(jsObj)
  }

  private def getOutcomeResponse(`type`: String, outcome: String, reason: Option[String] = None) = {

    var jsObj = Json.obj(
      JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
      JsonGeneralLabels.TYPE -> `type`,
      JsonGeneralLabels.OUTCOME -> outcome
    )

    if (reason.isDefined) jsObj = jsObj +(JsonGeneralLabels.REASON, JsString(reason.get))

    Json.stringify(jsObj)
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
