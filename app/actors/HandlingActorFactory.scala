package actors

import akka.actor.Props
import play.api.Logger
import actors.websocket.{ContentExchangeActorWS, PhotoExchangeActorWS}
import actors.http.{ContentExchangeActorHTTP, PhotoExchangeActorHTTP}
import consts.RequestTypes.RequestTypes
import consts.{Criteria, RequestTypes}
import consts.Criteria.Criteria

object HandlingActorFactory {
  // protocol type
  val HTTP = 0
  val WEBSOCKET = 1

  def getActorProps(requestType: RequestTypes, criteria: Criteria, protocolType: Int): Props = {

    protocolType match {
      case HTTP => {
        requestType match {

          case RequestTypes.PHOTO => {
            Logger.info(s"HandlingActorFactory, returning props for PHOTO")
            PhotoExchangeActorHTTP.props
          }

          case RequestTypes.CONTENT => {
            Logger.info(s"HandlingActorFactory, returning props for CONTENT")
            ContentExchangeActorHTTP.props
          }
        }
      }

      case WEBSOCKET => {
        requestType match {

          case RequestTypes.PHOTO => {
            Logger.info(s"HandlingActorFactory, returning props for PHOTO")
            PhotoExchangeActorWS.props
          }

          case RequestTypes.CONTENT => {

            criteria match {
              case Criteria.POSITION => {
                Logger.info(s"HandlingActorFactory, returning props for ${RequestTypes.CONTENT} - ${Criteria.POSITION}")
                ContentExchangeActorWS.props

              }
              case Criteria.PRESENCE => {
                Logger.info(s"HandlingActorFactory, returning props for ${RequestTypes.CONTENT} - ${Criteria.PRESENCE}")
                ContentExchangeActorWS.props
              }
            }
          }
        }
      }
    }
  }
}
