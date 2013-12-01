package controllers

import play.api.mvc.Action

object ApplicationHTTP extends MyController {

  // simple alive response
  def alive() = Action {
    request => Ok("I'm alive!\n")
  }

}
