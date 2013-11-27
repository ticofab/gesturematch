package actors

import akka.actor.Props
import play.api.Logger
import actors.websocket.{ContentExchangeActorWS, PhotoExchangeActorWS}
import actors.http.{ContentExchangeActorHTTP, PhotoExchangeActorHTTP}

object HandlingActorFactory {
  // protocol type
  val HTTP = 0
  val WEBSOCKET = 1

  // request type
  val PHOTO = "photo"
  val CONTENT = "content"

  def getValidRequestsTypes: List[String] = List(PHOTO, CONTENT)

  def getActorProps(requestType: String, protocolType: Int): Props = {

    protocolType match {
      case HTTP => {
        requestType match {

          case PHOTO => {
            Logger.info(s"HandlingActorFactory, returning props for PHOTO")
            PhotoExchangeActorHTTP.props
          }

          case CONTENT => {
            Logger.info(s"HandlingActorFactory, returning props for CONTENT")
            ContentExchangeActorHTTP.props
          }
        }
      }

      case WEBSOCKET => {
        requestType match {

          case PHOTO => {
            Logger.info(s"HandlingActorFactory, returning props for PHOTO")
            PhotoExchangeActorWS.props
          }

          case CONTENT => {
            Logger.info(s"HandlingActorFactory, returning props for CONTENT")
            ContentExchangeActorWS.props
          }

        }
      }
    }
  }
}
