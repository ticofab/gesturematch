package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxConnectionLifetime = 1.day
  val maxOldestRequestInterval = 4.seconds
  val maxOldestRequestIntervalMillis = maxOldestRequestInterval.toMillis
  val minIntervalBetweenSameDeviceRequestMillis = 1500.milliseconds.toMillis
}
