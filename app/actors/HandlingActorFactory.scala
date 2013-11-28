package actors

import akka.actor.Props
import play.api.Logger
import actors.websocket.{ContentExchangeActorWS, PhotoExchangeActorWS}
import consts.RequestTypes.RequestTypes
import consts.{Criteria, RequestTypes}
import consts.Criteria.Criteria

object HandlingActorFactory {

  def getActorProps(requestType: RequestTypes, criteria: Criteria): Props = {

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
