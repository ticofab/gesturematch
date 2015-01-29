package helpers.logging

import models.matching.base.MatchRequest

object LoggingHelper {
  def getNewRequestLogging(name: String, request: MatchRequest, possiblyMatchingRequests: Int, msg: String = "") = {
    s"$name, new ${request.toString}, matching it with $possiblyMatchingRequests existing requests --> $msg"
  }
}
