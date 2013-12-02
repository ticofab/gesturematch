package helpers

import models.RequestToMatch
import consts.{Areas, Criteria}
import consts.Criteria.Criteria
import consts.Areas.Areas

case class InvalidRequestException(message: String) extends Exception(message)

object RequestAnalyticsHelper {

  def requestIsValid(criteria: Criteria, areaStart: Areas, areaEnd: Areas) = {
    // TODO:
    //    check more things about parameters
    //    - api key must be valid
    //    - app id must be valid

    lazy val invReqHead = "Invalid request: invalid "
    lazy val allowedValues = ". Allowed values are: "

    if (criteria == Criteria.INVALID)
      throw InvalidRequestException(s"$invReqHead criteria $allowedValues ${Criteria.getValidOnes}")

    if (areaStart == Areas.INVALID)
      throw InvalidRequestException(s"$invReqHead areaStart $allowedValues ${Areas.getValidOnes}")

    if (areaEnd == Areas.INVALID)
      throw InvalidRequestException(s"$invReqHead areaEnd $allowedValues ${Areas.getValidOnes}")

    if (areaStart == areaEnd)
      throw InvalidRequestException("Invalid request: starting and ending areas are equal.")

    true
  }

  def requestsAreCompatible(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if equality parameters are the same
    val equality = r1.equalityParam == r2.equalityParam

    // check if apiKey is the same
    val sameApiKey = r1.apiKey == r2.apiKey

    // check if appId is the same
    val sameAppId = r1.appId == r2.appId

    closeEnough && equality && sameApiKey && sameAppId
  }
}
