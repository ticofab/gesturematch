package actors

import akka.actor.Props

/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 16:06
 */
object HandlingActorFactory {
  def getActorProps(requestType: String): Option[Props] = {
    requestType match {
      case "photo" => Some(PhotoExchangeActor.props)
      case "contact" => Some(ContactExchangeActor.props)
      case _ => None
    }
  }
}
