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

  // db test
  def dbTest() = Action {
    request => {

//      val db: DefaultDB = ReactiveMongoPlugin.db
//      val collezioni: Future[List[String]] = db.collectionNames
//      collezioni.map(l => println(l))

      val db: DefaultDB = ReactiveMongoPlugin.db
      val coll = db.collection[JSONCollection]("swipematch")
      val query = Json.obj("apiKey" -> 1, "appIds.appId" -> "swipeaim-example-android")
      val filter = Json.obj("_id" -> 1)
      val fu: Future[List[JsValue]] = coll.find(query, filter).cursor[JsValue].collect[List](1)
      fu.recover {
        case _ => false
      }
      val retrievedDocs: List[JsValue] = Await.result(fu, Timeouts.maxConnectionLifetime)
      Logger.debug(s"areKeyAndIdValid, retrieved ${retrievedDocs.length} documents.")
      if (retrievedDocs.length > 0) Logger.debug(s"${retrievedDocs.head}")
      retrievedDocs.length > 0

      Ok("DB test passed!\n")
    }
  }
}
