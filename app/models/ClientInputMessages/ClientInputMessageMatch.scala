package models.ClientInputMessages

import play.api.libs.json._
import play.api.libs.json.Reads._
import consts.json.JsonInputLabels

case class ClientInputMessageMatch(criteria: String,
                                   apiKey: String,
                                   appId: String,
                                   latitude: Double,
                                   longitude: Double,
                                   areaStart: String,
                                   areaEnd: String,
                                   deviceId: String,
                                   equalityParam: Option[String],
                                   temperature: Option[Int],
                                   wifiNetworks: Option[List[String]],
                                   orientation: Option[Double],
                                   swipeOrientation: Option[Double]) extends ClientInputMessage

object ClientInputMessageMatch {

  def fromJson(jsonValue: JsValue): ClientInputMessageMatch = {
    val criteria = (jsonValue \ JsonInputLabels.MATCH_INPUT_CRITERIA).as[String]
    val apiKey = (jsonValue \ JsonInputLabels.MATCH_INPUT_APIKEY).as[String]
    val appId = (jsonValue \ JsonInputLabels.MATCH_INPUT_APPID).as[String]
    val latitude = (jsonValue \ JsonInputLabels.MATCH_INPUT_LATITUDE).as[Double]
    val longitude = (jsonValue \ JsonInputLabels.MATCH_INPUT_LONGITUDE).as[Double]
    val areaStart = (jsonValue \ JsonInputLabels.MATCH_INPUT_AREASTART).as[String]
    val areaEnd = (jsonValue \ JsonInputLabels.MATCH_INPUT_AREAEND).as[String]
    val deviceId = (jsonValue \ JsonInputLabels.MATCH_INPUT_DEVICEID).as[String]
    val equalityParam = (jsonValue \ JsonInputLabels.MATCH_INPUT_EQUALITYPARAM).asOpt[String]
    val temperature = (jsonValue \ JsonInputLabels.MATCH_INPUT_TEMPERATURE).asOpt[Int]
    val wifiNetworks = (jsonValue \ JsonInputLabels.MATCH_INPUT_WIFI_NETWORKS).asOpt[List[String]]
    val orientation = (jsonValue \ JsonInputLabels.MATCH_INPUT_ORIENTATION).asOpt[Double]
    val swipeOrientation = (jsonValue \ JsonInputLabels.MATCH_INPUT_SWIPE_ORIENTATION).asOpt[Double]

    // if we got here, it means that everything is fine
    ClientInputMessageMatch(criteria, apiKey, appId, latitude, longitude, areaStart,
      areaEnd, deviceId, equalityParam, temperature, wifiNetworks, orientation, swipeOrientation)
  }

}




