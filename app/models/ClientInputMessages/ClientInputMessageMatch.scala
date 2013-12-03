package models.ClientInputMessages

import play.api.libs.json._
import play.api.libs.json.Reads._
import consts.json.JsonInputLabels


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

  def fromJson(jsonValue: JsValue): ClientInputMessageMatch = {
    val criteria = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_CRITERIA).asOpt[String]
    val apiKey = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_APIKEY).asOpt[String]
    val appId = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_APPID).asOpt[String]
    val latitude = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_LATITUDE).asOpt[String]
    val longitude = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_LONGITUDE).asOpt[String]
    val areaStart = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_AREASTART).asOpt[String]
    val areaEnd = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_AREAEND).asOpt[String]
    val deviceId = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_DEVICEID).asOpt[String]
    val equalityParam = (jsonValue \ JsonInputLabels.MATCH_MESSAGE_EQUALITYPARAM).asOpt[String]

    // if we got here, it means that everything is fine
    ClientInputMessageMatch(criteria.get, apiKey.get, appId.get, latitude.get.toDouble,
      longitude.get.toDouble, areaStart.get, areaEnd.get, deviceId.get, equalityParam.get)
  }

}




