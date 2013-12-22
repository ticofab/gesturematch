package helpers

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
}
