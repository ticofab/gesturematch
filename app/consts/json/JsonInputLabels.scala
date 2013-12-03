package consts.json

object JsonInputLabels {

  // input types
  val INPUT_TYPE = "type"
  val INPUT_TYPE_MATCH = "match"
  val INPUT_TYPE_LEAVE_GROUP = "leaveGroup"
  val INPUT_TYPE_DISCONNECT = "disconnect"
  val INPUT_TYPE_DELIVERY = "delivery"

  // input match keys
  val MATCH_MESSAGE_CRITERIA = "criteria"
  val MATCH_MESSAGE_APIKEY = "apiKey"
  val MATCH_MESSAGE_APPID = "appId"
  val MATCH_MESSAGE_LATITUDE = "latitude"
  val MATCH_MESSAGE_LONGITUDE = "longitude"
  val MATCH_MESSAGE_AREASTART = "areaStart"
  val MATCH_MESSAGE_AREAEND = "areaEnd"
  val MATCH_MESSAGE_DEVICEID = "deviceId"
  val MATCH_MESSAGE_EQUALITYPARAM = "equalityParam"

  // input reason
  val INPUT_REASON = "reason"
  val INPUT_PAYLOAD = "payload"
  val INPUT_RECIPIENTS = "recipients"
  val INPUT_RECIPIENT = "recipient"
}
