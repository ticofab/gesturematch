package actors

import akka.actor.Props
import play.api.Logger

object HandlingActorFactory {
  val PHOTO = "photo"
  val CONTENT = "content"

  def getValidRequests: List[String] = List(PHOTO, CONTENT)

  def getActorProps(requestType: String): Props = {
    requestType match {

      case PHOTO => {
        Logger.info(s"HandlingActorFactory, returning props for PHOTO")
        PhotoExchangeActor.props
      }

      case CONTENT => {
        Logger.info(s"HandlingActorFactory, returning props for CONTENT")
        ContentExchangeActor.props
      }

    }
  }
}
