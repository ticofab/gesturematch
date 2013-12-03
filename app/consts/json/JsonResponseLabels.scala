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
  val OUTCOME_NO_GROUP_TO_LEAVE = "noGroupToLeave"
  val OUTCOME_GROUP_LEFT = "groupLeft"

  // -- outcomes to delivery request
  val OUTCOME_PAYLOAD_EMPTY_GROUP = "notPartOfAnyGroup"
  val OUTCOME_PAYLOAD_PARTIALLY_DELIVERED = "partiallyDelivered"
  val OUTCOME_PAYLOAD_NOT_DELIVERED = "notDelivered"
  val OUTCOME_PAYLOAD_DELIVERED = "delivered"

  // matched messages labels
  val GROUP_SIZE = "groupSize"
  val ME_IN_GROUP = "meInGroup"
  val OTHERS_IN_GROUP = "othersInGroup"

  // matchee object
  val ID_IN_GROUP = "idInGroup"
  val POSITION_IN_GROUP = "position"
}
