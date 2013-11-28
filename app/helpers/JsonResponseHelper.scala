package helpers

import play.api.libs.json.Json
import consts.JsonLabels

object JsonResponseHelper {

  def getMatchedTouch(groupSize: Int) = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED_GROUP,
        JsonLabels.GROUP_SIZE -> groupSize
      )
    )
  }

  def getMatched2ContentResponse = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED2
      )
    )
  }

  def getMatched4ContentResponse = {
    Json.stringify(
      Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED4
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
