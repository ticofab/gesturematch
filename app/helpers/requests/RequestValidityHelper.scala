/*
 * Copyright 2014-2015 Fabio Tiriticco, Fabway
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

import consts.{Areas, MatchCriteria}
import models.messages.client.{ClientInputMsgGroupCreateMatch, ClientInputMsgGroupComMatch}
import models.messages.client.base.ClientInputMatchRequest

case class InvalidRequestException(message: String) extends Exception(message)

object RequestValidityHelper {

  def matchRequestIsValid(matchInput: ClientInputMatchRequest, groupId: String): Boolean = {

    lazy val invReqHead = "Invalid request: invalid "
    lazy val allowedValues = ". Allowed values are: "

    val areaStart = Areas.getAreaFromString(matchInput.areaStart)
    val areaEnd = Areas.getAreaFromString(matchInput.areaEnd)
    val criteria = MatchCriteria.getMatchCriteriaFromString(matchInput.criteria)

    if (criteria == MatchCriteria.INVALID)
      throw InvalidRequestException(s"$invReqHead criteria $allowedValues ${MatchCriteria.getValidOnes}")

    if (areaStart == Areas.INVALID)
      throw InvalidRequestException(s"$invReqHead areaStart $allowedValues ${Areas.getValidOnes}")

    if (areaEnd == Areas.INVALID)
      throw InvalidRequestException(s"$invReqHead areaEnd $allowedValues ${Areas.getValidOnes}")

    if (areaStart == areaEnd)
      throw InvalidRequestException("Invalid request: starting and ending areas are equal.")

    // specific test for this request
    matchInput match {
      case mCom: ClientInputMsgGroupComMatch =>
        if (mCom.groupId.isEmpty)
          throw InvalidRequestException("Invalid request: groupId must be valid")

        val receivedGroupId = mCom.groupId.getOrElse("")
        if (groupId == "" || groupId != receivedGroupId)
          throw InvalidRequestException(s"Invalid request: device is not part of group $receivedGroupId")

        if (mCom.idInGroup.isEmpty)
          throw InvalidRequestException("Invalid request: myId must be a valid number")

      case gCre: ClientInputMsgGroupCreateMatch => // do nothing
    }

    true
  }
}
