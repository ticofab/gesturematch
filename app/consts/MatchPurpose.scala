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

package consts

import scala.util.Try

object MatchPurpose extends Enumeration {
    type MatchPurpose = Value
    val INVALID = Value("invalid")
    val GROUP_CREATION = Value("groupCreation")
    val GROUP_COMMUNICATION = Value("groupCommunication")

    def getMatchPurposeFromString(purpose: String) = Try(MatchCriteria.withName(purpose)) getOrElse INVALID

    def getValidOnes = this.values.filter(_ != INVALID)
}
