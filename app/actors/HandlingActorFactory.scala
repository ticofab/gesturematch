package actors

import akka.actor.Props
import play.api.Logger

object HandlingActorFactory {
  val PHOTO = "photo"
  val CONTACT = "contact"
  def getActorProps(requestType: String): Props = {
    requestType match {

      case PHOTO => {
        Logger.info(s"HandlingActorFactory, returning props for PHOTO")
        PhotoExchangeActor.props
      }

      case CONTACT => {
        Logger.info(s"HandlingActorFactory, returning props for CONTACT")
        ContactExchangeActor.props
      }

    }
  }
}
