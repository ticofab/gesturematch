package consts

import scala.util.Try

object Criteria extends Enumeration {
  type Criteria = Value
  val INVALID = Value("invalid")
  val SWIPE = Value("swipe")
  val AIM = Value("aim")
  val PINCH = Value("pinch")

  def getCriteriaFromString(criteria: String) = Try(Criteria.withName(criteria)) getOrElse INVALID

  def getValidOnes = this.values.filter(_ != INVALID)
}
