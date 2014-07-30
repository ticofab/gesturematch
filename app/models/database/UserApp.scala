package models.database

import reactivemongo.bson.BSONDocument

case class UserApp(id: String, name: String)

object UserApp {
  def fromBSON(doc: BSONDocument): UserApp = {
    val appId = doc.getAs[String]("appId").getOrElse("")
    val appName = doc.getAs[String]("appName").getOrElse("")
    UserApp(appId, appName)
  }
}
