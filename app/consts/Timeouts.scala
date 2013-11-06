package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxOldestRequestInterval = 50.seconds // 5.seconds
  val maxOldestRequestIntervalMillis = maxOldestRequestInterval.toMillis
  val minIntervalBetweenSameDeviceRequestMillis = 1500.milliseconds.toMillis
}
