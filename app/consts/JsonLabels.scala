package consts

object JsonLabels {
  // possible outcomes
  val OUTCOME = "outcome"
  val OUTCOME_MATCHED = "matched"
  val OUTCOME_UNCERTAIN = "uncertain"
  val OUTCOME_TIMEOUT = "timeout"
  val OUTCOME_UNKNOWN_ERROR = "error"

  // matched messages labels
  val GROUP_SIZE = "groupSize"
  val MY_CONNECTION_INFO = "myConnectionInfo"
  val OTHERS_CONNECTION_INFO = "othersConnectionInfo"

  // info object
  val ID_IN_GROUP = "idInGroup"
  val POSITION_IN_GROUP = "position"

  // payload of the matching devices.
  val PAYLOAD = "payload"
  val FIRST_DEVICE = "a"
  val SECOND_DEVICE = "b"
  val THIRD_DEVICE = "c"
}
