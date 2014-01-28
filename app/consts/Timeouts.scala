package consts

import scala.concurrent.duration.DurationInt

object Timeouts {
  val maxDatabaseResponseTime = 1.seconds
  val maxConnectionLifetime = 750.seconds // 15 minutes
  val maxOldestRequestInterval = 3.seconds
  val maxOldestRequestIntervalMillis = maxOldestRequestInterval.toMillis
}
