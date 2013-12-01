package models.ClientInputMessages

import play.api.libs.json._
import play.api.libs.json.Reads._


// TODO
case class ClientInputMessageMatch(criteria: String,
                                   apiKey: String,
                                   appId: String,
                                   latitude: Double,
                                   longitude: Double,
                                   areaStart: String,
                                   areaEnd: String,
                                   deviceId: String,
                                   equalityParam: String) extends ClientInputMessage

object ClientInputMessageMatch {

  val MATCH_MESSAGE_CRITERIA = "criteria"
  val MATCH_MESSAGE_APIKEY = "apiKey"
  val MATCH_MESSAGE_APPID = "appId"
  val MATCH_MESSAGE_LATITUDE = "latitude"
  val MATCH_MESSAGE_LONGITUDE = "longitude"
  val MATCH_MESSAGE_AREASTART = "areaStart"
  val MATCH_MESSAGE_AREAEND = "areaEnd"
  val MATCH_MESSAGE_DEVICEID = "deviceId"
  val MATCH_MESSAGE_EQUALITYPARAM = "equalityParam"

  def fromJson(jsonValue: JsValue): ClientInputMessageMatch = {
    val criteria = (jsonValue \ MATCH_MESSAGE_CRITERIA).asOpt[String]
    val apiKey = (jsonValue \ MATCH_MESSAGE_APIKEY).asOpt[String]
    val appId = (jsonValue \ MATCH_MESSAGE_APPID).asOpt[String]
    val latitude = (jsonValue \ MATCH_MESSAGE_LATITUDE).asOpt[Double]
    val longitude = (jsonValue \ MATCH_MESSAGE_LONGITUDE).asOpt[Double]
    val areaStart = (jsonValue \ MATCH_MESSAGE_AREASTART).asOpt[String]
    val areaEnd = (jsonValue \ MATCH_MESSAGE_AREAEND).asOpt[String]
    val deviceId = (jsonValue \ MATCH_MESSAGE_DEVICEID).asOpt[String]
    val equalityParam = (jsonValue \ MATCH_MESSAGE_EQUALITYPARAM).asOpt[String]

    //if we got here, it means that everything is fine
    ClientInputMessageMatch(criteria.get, apiKey.get, appId.get, latitude.get,
      longitude.get, areaStart.get, areaEnd.get, deviceId.get, equalityParam.get)
  }

}




