package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxConnectionLifetime = 1.day
  val maxOldestRequestInterval = 50.seconds // 4.seconds
  val maxDatabaseResponseTime = 5.seconds // 1.seconds
  val maxOldestRequestIntervalMillis = maxOldestRequestInterval.toMillis
  val minIntervalBetweenSameDeviceRequestMillis = 1500.milliseconds.toMillis
}
