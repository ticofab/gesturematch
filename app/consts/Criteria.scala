package consts

import scala.util.Try

object Criteria extends Enumeration {
  type Criteria = Value
  val INVALID = Value("invalid")
  val POSITION = Value("position")
  val PRESENCE = Value("presence")

  def getCriteriaFromString(criteria: String) = Try(Criteria.withName(criteria)) getOrElse(INVALID)

  def getValidOnes = this.values.filter(_ != INVALID)
}
