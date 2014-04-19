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

package helpers.requests

import consts.{Areas, Criteria}
import consts.Criteria.Criteria
import consts.Areas._

case class InvalidRequestException(message: String) extends Exception(message)

object RequestValidityHelper {

  def matchRequestIsValid(criteria: Criteria, areaStart: Areas, areaEnd: Areas): Boolean = {

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
