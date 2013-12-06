package traits

import scala.util.Random

trait StringGenerator {
  def getGroupUniqueString: String = Random.alphanumeric.take(8).mkString
}

