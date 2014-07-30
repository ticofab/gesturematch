/*
 * Copyright 2014 Fabio Tiriticco, Fabway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helpers.json

import models.matching.Matchee
import models.scheme.Scheme
import play.api.libs.json._
import consts.json.{JsonErrorLabels, JsonInputLabels, JsonGeneralLabels, JsonResponseLabels}
import scala.Some

object JsonResponseHelper {

  def createMatchedResponse(myself: Matchee, allMatchees: List[Matchee], groupId: String,
                             scheme: Option[Scheme]) = {

    val objList: List[Int] = allMatchees.map(info => info.idInGroup)
    val jsonArray: JsValue = Json.toJson(objList)

      var jsObj = Json.obj(
        JsonGeneralLabels.KIND -> JsonResponseLabels.KIND_RESPONSE,
        JsonGeneralLabels.TYPE -> JsonInputLabels.INPUT_TYPE_MATCH,
        JsonGeneralLabels.OUTCOME -> JsonGeneralLabels.OK,
        JsonGeneralLabels.GROUP_ID -> JsString(groupId),
        JsonResponseLabels.MYSELF_IN_GROUP -> JsNumber(myself.idInGroup),
        JsonResponseLabels.OTHERS_IN_GROUP -> jsonArray
      )

    if (scheme.isDefined) {
      jsObj = jsObj + (JsonResponseLabels.GROUP_POSITION_SCHEME, Scheme.toJson(scheme.get))
    }

    Json.stringify(jsObj)
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
}
