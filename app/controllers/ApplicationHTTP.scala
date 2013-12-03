package controllers

import play.api.mvc.{Controller, Action}

object ApplicationHTTP extends Controller {

  // simple alive response
  def alive() = Action {
    request => Ok("I'm alive!\n")
  }
}
