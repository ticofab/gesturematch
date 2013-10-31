package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxOldestRequestTO = 30.seconds // 5.seconds
  val maxOldestRequestTOMillis = maxOldestRequestTO.toMillis
}
