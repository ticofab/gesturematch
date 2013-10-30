package consts

import scala.concurrent.duration.DurationInt

object TimeoutConsts {
  val maxOldestRequestTO = 5 seconds
  val maxOldestRequestTOMillis = maxOldestRequestTO toMillis
  val actorSimpleRequestTO = maxOldestRequestTO
  val actorFileTransferTO = 10 seconds
}