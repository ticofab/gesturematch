package helpers.storage

import java.text.SimpleDateFormat

import models.database.{SessionUser, User}
import play.api.Logger
import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object DBHelper {
  private val db: DefaultDB = ReactiveMongoPlugin.db
  private val coll: BSONCollection = db.collection[BSONCollection]("apiKeys")
  private val dateFormatter: SimpleDateFormat = new java.text.SimpleDateFormat("MMMyyyy")

  def getSessionUser(apiKey: String, appId: String): Future[Option[SessionUser]] = {
    // 1. get the user from the db
    val query = User.getQuery(apiKey, appId)
    val filter = User.getFilter
    val fu: Future[List[User]] = coll.find(query, filter).cursor[User].collect[List](2)

    // 2. convert the user to the SessionUser
    fu map {
      case Nil => None // invalid credentials!
      case user :: Nil => SessionUser.fromUser(user, appId)
      case user :: tail => None // too many results!
    }
  }

  private def getMonthYear: String = dateFormatter.format(new java.util.Date())

  private def updateOp(apiKey: String, appId: String, modifier: BSONDocument) = {
    val query = BSONDocument("apiKey" -> apiKey, s"appIds.appId" -> appId)
    val fut: Future[LastError] = coll.update(query, modifier)
    fut.onComplete {
      case Success(le) => if (!le.ok) Logger.debug(s"DB op failed, modifier: $modifier")
      case Failure(e) => ??? // TODO
    }
  }

  def addMatchRequest(apiKey: String, appId: String) = {
    val monthYear = getMonthYear
    val modifier = BSONDocument("$inc" -> BSONDocument(s"appIds.$$.$monthYear.matchAttempts" -> 1))
    Logger.debug(s"add connection request, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addMatchEstablished(apiKey: String, appId: String, nrMatches: Int) = {
    val monthYear = getMonthYear
    val modifier = BSONDocument("$inc" -> BSONDocument(s"appIds.$$.$monthYear.matchesEstablished" -> nrMatches))
    Logger.debug(s"add connection established, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addPayloadDelivered(apiKey: String, appId: String, payloadBytes: Int) = {
    val monthYear = getMonthYear
    val modifier = BSONDocument("$inc" -> BSONDocument(s"appIds.$$.$monthYear.payloadDelivered" -> payloadBytes))
    Logger.debug(s"add payload delivered, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }

  def addPayloadReceived(apiKey: String, appId: String, payloadBytes: Int) = {
    val monthYear = getMonthYear
    val modifier = BSONDocument("$inc" -> BSONDocument(s"appIds.$$.$monthYear.payloadReceived" -> payloadBytes))
    Logger.debug(s"add payload sent, modifier: $modifier")
    updateOp(apiKey, appId, modifier)
  }
}
