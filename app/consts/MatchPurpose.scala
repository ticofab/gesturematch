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
