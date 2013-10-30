package actors

import akka.actor.Props

/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 16:06
 */
object HandlingActorFactory {
  val PHOTO = "photo"
  val CONTACT = "contact"
  def getActorProps(requestType: String): Props = {
    requestType match {
      case PHOTO => PhotoExchangeActor.props
      case CONTACT => ContactExchangeActor.props
    }
  }
}
