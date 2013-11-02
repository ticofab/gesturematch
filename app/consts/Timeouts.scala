package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxOldestRequestInterval = 30.seconds // 5.seconds
  val maxOldestRequestIntervalMillis = maxOldestRequestInterval.toMillis
  val minIntervalBetweenSameDeviceRequestMillis = 1500.milliseconds.toMillis
}
