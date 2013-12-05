package consts.json

object JsonMessageLabels {
  // message kind key
  val KIND_MESSAGE = "message"

  // message type keys
  val MESSAGE_TYPE_DELIVERY = "delivery"
  val MESSAGE_TYPE_MATCHEE_LEFT_GROUP = "matcheeLeft"

  // message extra keys
  val MESSAGE_MATCHEE_ID = "matcheeId"


  // ideas:
  // - when a matchee leaves the group, don't destroy it but let the clients know how many
  //    matchees are left in the group
}
