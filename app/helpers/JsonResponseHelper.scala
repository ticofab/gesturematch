package helpers

import play.api.libs.json.Json
import consts.{MatcheeInfo, JsonLabels}

object JsonResponseHelper {

  def getMatchedTouch(groupSize: Int) = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED_GROUP,
        JsonLabels.GROUP_SIZE -> groupSize
      )
    )
  }

  def getMatched2ContentResponse(matcheeInfo: List[MatcheeInfo]) = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED2,
        JsonLabels.PAYLOAD -> matcheeInfo.head._2
      )
    )
  }

  def getMatched4ContentResponse(matcheesInfo: List[MatcheeInfo]) = {
    val matcheesInfoVector = matcheesInfo.toVector
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED4,
        JsonLabels.PAYLOAD -> Json.arr(
          Json.obj(
            JsonLabels.FIRST_DEVICE -> matcheesInfoVector(0)._2,
            JsonLabels.SECOND_DEVICE -> matcheesInfoVector(1)._2,
            JsonLabels.THIRD_DEVICE -> matcheesInfoVector(2)._2
          )
        )
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
