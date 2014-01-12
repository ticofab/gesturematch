package helpers.storage

import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.{JsObject, JsValue, Json}
import reactivemongo.api.DefaultDB
import play.modules.reactivemongo.ReactiveMongoPlugin
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import play.api.Logger
import reactivemongo.core.commands.LastError
import scala.util.{Failure, Success}

// implicits

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

object DBHelper {
  private val db: DefaultDB = ReactiveMongoPlugin.db
  private val coll: JSONCollection = db.collection[JSONCollection]("swipematch")

  def areKeyAndIdValid(apiKey: String, appId: String): Boolean = {
    val query = Json.obj("apiKey" -> apiKey, "appIds.appId" -> appId)
    val filter = Json.obj("_id" -> 1)

    val fu: Future[List[JsValue]] = coll.find(query, filter).cursor[JsValue].collect[List](1)
    fu.recover {
      case _ => false
    }

    val retrievedDocs: List[JsValue] = Await.result(fu, 1.seconds)
    Logger.debug(s"areKeyAndIdValid: ($apiKey, $appId), retrieved ${retrievedDocs.length} documents.")
    retrievedDocs.length > 0
  }

  private def updateOp(apiKey: String, appId: String, modifier: JsObject) = {
    val query = Json.obj("apiKey" -> apiKey, "appIds.appId" -> appId)
    val fut: Future[LastError] = coll.update(query, modifier)
    fut.onComplete {
      case Success(le) => if (!le.ok) Logger.debug(s"DB op failed, modifier: $modifier")
      case Failure(e) => ??? // TODO
    }
  }

  def addMatchRequest(apiKey: String, appId: String) = {
    val modifier = Json.obj("$inc" -> Json.obj("appIds.$.connectionAttempts" -> 1))
    Logger.debug(s"add connection request, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addMatchEstablished(apiKey: String, appId: String, nrConnections: Int) = {
    val modifier = Json.obj("$inc" -> Json.obj("appIds.$.connectionsEstablished" -> nrConnections))
    Logger.debug(s"add connection established, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addPayloadDelivered(apiKey: String, appId: String, payloadBytes: Int) = {
    val modifier = Json.obj("$inc" -> Json.obj("appIds.$.payloadSent" -> payloadBytes))
    Logger.debug(s"add payload delivered, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addPayloadSent(apiKey: String, appId: String, payloadBytes: Int) = {
    val modifier = Json.obj("$inc" -> Json.obj("appIds.$.payloadDelivered" -> payloadBytes))
    Logger.debug(s"add payload sent, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }
}
