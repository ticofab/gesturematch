package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxOldestRequestTO = 5.seconds
  val maxOldestRequestTOMillis = maxOldestRequestTO.toMillis
}