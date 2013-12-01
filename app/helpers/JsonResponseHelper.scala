package helpers

import play.api.libs.json.{JsObject, Json}
import consts.JsonLabels
import models.MatcheeInfo

object JsonResponseHelper {

  def createMatchedResponse(myInfo: MatcheeInfo, otherInfos: List[MatcheeInfo]) = {

    val objList: List[JsObject] = otherInfos.map(info => MatcheeInfo.getInfoObj(info))
    val jsonArray = Json.toJson(objList)

    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED,
        JsonLabels.GROUP_SIZE -> (otherInfos.size + 1),
        JsonLabels.MY_CONNECTION_INFO -> MatcheeInfo.getInfoObj(myInfo),
        JsonLabels.OTHERS_CONNECTION_INFO -> jsonArray
      )
    )

  }

  def getTimeoutResponse = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_TIMEOUT
      )
    )
  }

  def getUnknownErrorResponse = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_UNKNOWN_ERROR
      )
    )
  }

}
