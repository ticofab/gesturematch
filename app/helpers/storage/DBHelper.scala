package helpers.storage

import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.{JsValue, Json}
import reactivemongo.api.DefaultDB
import play.modules.reactivemongo.ReactiveMongoPlugin
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import play.api.Logger

// implicits
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

object DBHelper {
  def areKeyAndIdValid(apiKey: String, appId: String): Boolean = {
    val db: DefaultDB = ReactiveMongoPlugin.db
    val coll = db.collection[JSONCollection]("swipematch")
    val filter = Json.obj("_id" -> 1)
    val query = Json.obj("apiKey" -> apiKey, "appIds.appId" -> appId)

    val fu: Future[List[JsValue]] = coll.find(query, filter).cursor[JsValue].collect[List](1)
    fu.recover {
      case _ => false
    }

    val retrievedDocs: List[JsValue] = Await.result(fu, 1.seconds)
    Logger.debug(s"areKeyAndIdValid: ($apiKey, $appId), retrieved ${retrievedDocs.length} documents.")
    retrievedDocs.length > 0
  }
}
