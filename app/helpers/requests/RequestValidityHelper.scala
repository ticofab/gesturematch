package helpers.requests

import consts.{Areas, Criteria}
import consts.Criteria.Criteria
import consts.Areas.Areas
import helpers.storage.DBHelper

case class InvalidRequestException(message: String) extends Exception(message)

object RequestValidityHelper {

  def requestIsValid(apiKey: String, appId: String, criteria: Criteria, areaStart: Areas,
                     areaEnd: Areas, swipeOrientation: Option[Double]) = {

    lazy val invReqHead = "Invalid request: invalid "
    lazy val allowedValues = ". Allowed values are: "

    if (!DBHelper.areKeyAndIdValid(apiKey, appId))
      throw new InvalidRequestException(s"ApiKey and AppId pair ($apiKey, $appId) is not valid")

    if (criteria == Criteria.INVALID)
      throw InvalidRequestException(s"$invReqHead criteria $allowedValues ${Criteria.getValidOnes}")

    if (areaStart == Areas.INVALID)
      throw InvalidRequestException(s"$invReqHead areaStart $allowedValues ${Areas.getValidOnes}")

    if (areaEnd == Areas.INVALID)
      throw InvalidRequestException(s"$invReqHead areaEnd $allowedValues ${Areas.getValidOnes}")

    if (areaStart == areaEnd)
      throw InvalidRequestException("Invalid request: starting and ending areas are equal.")

    if (criteria == Criteria.AIM && swipeOrientation.isEmpty)
      throw InvalidRequestException("Invalid request: when using AIM criteria, the swipeOrientation must be provided")

    true
  }
}
