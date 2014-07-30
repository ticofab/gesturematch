package models.database

import reactivemongo.bson._

case class User(name: String, apps: List[UserApp])

object User {
  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = {
      val name = doc.getAs[String]("name").getOrElse("")
      val apps: List[UserApp] = doc.getAs[List[BSONDocument]]("appIds").getOrElse(Nil).map(UserApp.fromBSON)
      User(name, apps)
    }
  }

  def getQuery(apiKey: String, appId: String) = BSONDocument("apiKey" -> apiKey, "appIds.appId" -> appId)
  def getFilter = BSONDocument("_id" -> 0, "name" -> 1, "appIds" -> 1)
}