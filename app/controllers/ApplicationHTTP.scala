package controllers

import play.api.mvc.Action
import scala.concurrent.Future


object ApplicationHTTP extends MyController {
  def requestHTTP(`type`: String,
                  apiKey: String,
                  appId: String,
                  latitude: Double,
                  longitude: Double,
                  swipeStart: Int,
                  swipeEnd: Int,
                  deviceId: String,
                  equalityParam: String,
                  payload: String) = Action.async {

    val result = Ok
    Future.successful(result)
  }

}
