package helpers.storage

import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.{JsObject, JsValue, Json}
import reactivemongo.api.DefaultDB
import play.modules.reactivemongo.ReactiveMongoPlugin
import scala.concurrent.{Await, Future}
import play.api.Logger
import reactivemongo.core.commands.LastError
import scala.util.{Failure, Success}
import consts.Timeouts
import java.util.concurrent.TimeoutException
import java.text.SimpleDateFormat

// implicits

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

object DBHelper {
  private val db: DefaultDB = ReactiveMongoPlugin.db
  private val coll: JSONCollection = db.collection[JSONCollection]("apiKeys")
  private val dateFormatter: SimpleDateFormat = new java.text.SimpleDateFormat("MMMyyyy")

  def areKeyAndIdValid(apiKey: String, appId: String): Future[Boolean] = {
    val query = Json.obj("apiKey" -> apiKey, "appIds.appId" -> appId)
    val filter = Json.obj("_id" -> 1)
    val fu: Future[List[JsValue]] = coll.find(query, filter).cursor[JsValue].collect[List](1)
    fu.map(_.length > 0)
  }

  private def getMonthYear: String = dateFormatter.format(new java.util.Date())

  private def updateOp(apiKey: String, appId: String, modifier: JsObject) = {
    val query = Json.obj("apiKey" -> apiKey, s"appIds.appId" -> appId)
    val fut: Future[LastError] = coll.update(query, modifier)
    fut.onComplete {
      case Success(le) => if (!le.ok) Logger.debug(s"DB op failed, modifier: $modifier")
      case Failure(e) => ??? // TODO
    }
  }

  def addMatchRequest(apiKey: String, appId: String) = {
    val monthYear = getMonthYear
    val modifier = Json.obj("$inc" -> Json.obj(s"appIds.$$.$monthYear.matchAttempts" -> 1))
    Logger.debug(s"add connection request, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addMatchEstablished(apiKey: String, appId: String, nrMatches: Int) = {
    val monthYear = getMonthYear
    val modifier = Json.obj("$inc" -> Json.obj(s"appIds.$$.$monthYear.matchesEstablished" -> nrMatches))
    Logger.debug(s"add connection established, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addPayloadDelivered(apiKey: String, appId: String, payloadBytes: Int) = {
    val monthYear = getMonthYear
    val modifier = Json.obj("$inc" -> Json.obj(s"appIds.$$.$monthYear.payloadDelivered" -> payloadBytes))
    Logger.debug(s"add payload delivered, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addPayloadReceived(apiKey: String, appId: String, payloadBytes: Int) = {
    val monthYear = getMonthYear
    val modifier = Json.obj("$inc" -> Json.obj(s"appIds.$$.$monthYear.payloadReceived" -> payloadBytes))
    Logger.debug(s"add payload sent, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }
}
