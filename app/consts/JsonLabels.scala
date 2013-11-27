package consts

object JsonLabels {
  // possible outcomes
  val OUTCOME = "outcome"
  val OUTCOME_MATCHED2 = "matched2"
  val OUTCOME_MATCHED4 = "matched4"
  val OUTCOME_UNCERTAIN = "uncertain"
  val OUTCOME_TIMEOUT = "timeout"
  val OUTCOME_UNKNOWN_ERROR = "error"
  val OUTCOME_MATCHED_GROUP = "matchedGroup"

  // group messages labels
  val GROUP_SIZE = "groupSize"

  // payload of the matching devices.
  val PAYLOAD = "payload"
  val FIRST_DEVICE = "a"
  val SECOND_DEVICE = "b"
  val THIRD_DEVICE = "c"
}
