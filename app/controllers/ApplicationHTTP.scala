package controllers

import play.api.mvc.{Controller, Action}
import play.api.Logger
import scala.concurrent.{Await, Future}
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.{JsValue, Json}
import consts.Timeouts

// implicits
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

// mongo db plugin
import reactivemongo.api.DefaultDB
import play.modules.reactivemongo.ReactiveMongoPlugin

object ApplicationHTTP extends Controller {

  // simple alive response
  def alive() = Action {
    request => {
      Logger.info(s"Alive request at ApplicationHTTP, request: $request")
      Ok("I'm alive!\n")
    }
  }
}
