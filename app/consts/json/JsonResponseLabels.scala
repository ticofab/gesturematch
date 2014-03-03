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
  val REASON_NOT_PART_OF_THIS_GROUP = "notPartOfThisGroup"

  // -- reasons to delivery request
  val REASON_PAYLOAD_NOT_DELIVERED = "notDelivered"

  // matched messages labels
  val MYSELF_IN_GROUP = "myId"
  val OTHERS_IN_GROUP = "group"
  val GROUP_POSITION_SCHEME = "scheme"

}
