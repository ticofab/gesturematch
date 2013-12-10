package controllers

import play.api.mvc.{Controller, Action}
import play.api.Logger

object ApplicationHTTP extends Controller {

  // simple alive response
  def alive() = Action {
    request => {
      Logger.info(s"Alive request at ApplicationHTTP, request: $request")
      Ok("I'm alive!\n")
    }
  }
}
