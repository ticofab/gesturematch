package consts.json

object JsonResponseLabels {
  // response kinds key
  val KIND_RESPONSE = "response"
  val KIND_INVALID_INPUT = "invalidInput"

  // -- reasons to match request
  val REASON_INVALID_REQUEST = "invalidRequest"
  val REASON_UNCERTAIN = "uncertain"
  val REASON_TIMEOUT = "timeout"
  val REASON_UNKNOWN_ERROR = "error"

  // -- reasons to leave group request
  val REASON_NOT_PART_OF_ANY_GROUP = "notPartOfAnyGroup"

  // -- reasons to delivery request
  val REASON_PAYLOAD_PARTIALLY_DELIVERED = "partiallyDelivered"
  val REASON_PAYLOAD_NOT_DELIVERED = "notDelivered"
  val REASON_PAYLOAD_DELIVERED = "delivered"

  // matched messages labels
  val GROUP_SIZE = "groupSize"
  val ME_IN_GROUP = "myselfInGroup"
  val OTHERS_IN_GROUP = "othersInGroup"

  // matchee object
  val ID_IN_GROUP = "idInGroup"
  val POSITION_IN_GROUP = "position"
}
