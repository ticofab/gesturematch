package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  // keep alive timeouts
  val keepAlivePingInterval = 29500.milliseconds

  // request timeouts
  val maxConnectionLifetime = 1.day
  val maxOldestRequestInterval = 50.seconds // 4.seconds
  val maxOldestRequestIntervalMillis = maxOldestRequestInterval.toMillis
  val minIntervalBetweenSameDeviceRequestMillis = 1500.milliseconds.toMillis

  // database timeouts
  val maxDatabaseResponseTime = 5.seconds // 1.seconds
}
