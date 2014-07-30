package models.database

import scala.collection.immutable.Nil

case class SessionUser(name: String, appName: String)

object SessionUser {
  def fromUser(user: User, appId: String): Option[SessionUser] = {
    if (user.name == "" || user.apps.isEmpty) None
    else user.apps.filter(_.id == appId) match {
      case Nil => None // nothing?
      case head :: Nil => Some(SessionUser(user.name, head.name))
      case head :: tail => None // too many!
    }
  }
}
