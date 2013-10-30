package actors

import akka.actor.Props

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
