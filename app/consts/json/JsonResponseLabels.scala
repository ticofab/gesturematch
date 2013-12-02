package consts.json

object JsonResponseLabels {
  // possible outcomes
  val OUTCOME = "outcome"

  // -- outcomes to match request
  val OUTCOME_INVALID_REQUEST = "invalidRequest"
  val OUTCOME_REASON = "reason"
  val OUTCOME_MATCHED = "matched"
  val OUTCOME_UNCERTAIN = "uncertain"
  val OUTCOME_TIMEOUT = "timeout"
  val OUTCOME_UNKNOWN_ERROR = "error"
  val OUTCOME_INPUT_INVALID = "invalid_input"

  // -- outcomes to disconnect request
  val OUTCOME_DISCONNECTED = "disconnected"

  // -- outcomes to break match request
  val OUTCOME_NO_MATCH_TO_BREAK = "nothingToBreak"
  val OUTCOME_MATCH_BROKEN = "matchBroken"

  // -- outcomes to delivery request
  val OUTCOME_PAYLOAD_DELIVERED = "delivered"

  // matched messages labels
  val GROUP_SIZE = "groupSize"
  val MY_CONNECTION_INFO = "myConnectionInfo"
  val OTHERS_CONNECTION_INFO = "othersConnectionInfo"

  // info object
  val ID_IN_GROUP = "idInGroup"
  val POSITION_IN_GROUP = "position"
}
