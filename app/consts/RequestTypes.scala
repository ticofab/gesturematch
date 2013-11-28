package consts

import scala.util.Try

object RequestTypes extends Enumeration {
  type RequestTypes = Value
  val PHOTO = Value("photo")
  val CONTENT = Value("content")
  val INVALID = Value

  def getTypeFromString(typeStr: String) = Try(RequestTypes.withName(typeStr)) getOrElse (INVALID)

  def getValidOnes = this.values.filter(_ != INVALID)
}
