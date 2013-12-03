package consts.json

object JsonMessageLabels {
  val MESSAGE_TYPE = "messageType"
  val MESSAGE_TYPE_DELIVERY = "delivery"
  val MESSAGE_TYPE_MATCHEE_LEFT_GROUP = "matcheeLeftGroup"

  val MATCHEE_ID = "matcheeId"
  val PAYLOAD = "payload"

  // ideas:
  // - when a matchee leaves the group, don't destroy it but let the clients know how many
  //    matchees are left in the group
}
