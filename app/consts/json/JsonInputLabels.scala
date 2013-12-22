package consts.json

object JsonInputLabels {
  // input type key
  val TYPE_INPUT = "input"

  // input type keys
  val INPUT_TYPE_MATCH = "match"
  val INPUT_TYPE_LEAVE_GROUP = "leaveGroup"
  val INPUT_TYPE_DISCONNECT = "disconnect"
  val INPUT_TYPE_DELIVERY = "delivery"

  // input match keys
  val MATCH_INPUT_CRITERIA = "criteria"
  val MATCH_INPUT_APIKEY = "apiKey"
  val MATCH_INPUT_APPID = "appId"
  val MATCH_INPUT_LATITUDE = "latitude"
  val MATCH_INPUT_LONGITUDE = "longitude"
  val MATCH_INPUT_AREASTART = "areaStart"
  val MATCH_INPUT_AREAEND = "areaEnd"
  val MATCH_INPUT_DEVICEID = "deviceId"
  val MATCH_INPUT_EQUALITYPARAM = "equalityParam"
  val MATCH_INPUT_TEMPERATURE = "temperature"
  val MATCH_INPUT_WIFI_NETWORKS = "wifiNetworks"

  // input delivery keys
  val INPUT_RECIPIENTS = "recipients"
  val INPUT_CHUNK_NUMBER = "chunkNr"
  val INPUT_TOTAL_CHUNKS = "totalChunks"
  val INPUT_DELIVERY_ID = "deliveryId"
}
