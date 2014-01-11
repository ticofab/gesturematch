package controllers

import play.api.mvc.{Controller, Action}
import play.api.Logger
import scala.concurrent.Future

// implicits
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

// mongo db plugin
import reactivemongo.api.{DefaultDB, MongoDriver}
import play.modules.reactivemongo.ReactiveMongoPlugin

object ApplicationHTTP extends Controller {

  // simple alive response
  def alive() = Action {
    request => {
      Logger.info(s"Alive request at ApplicationHTTP, request: $request")
      Ok("I'm alive!\n")
    }
  }

  // db test
  def dbTest() = Action {
    request => {

      val db: DefaultDB = ReactiveMongoPlugin.db
      val collezioni: Future[List[String]] = db.collectionNames
      collezioni.map(l => println(l))

      Ok("DB test passed!\n")
    }
  }
}
