package actors

import akka.actor.Props
import play.api.Logger
import actors.websocket.{ContentExchangeActorWS, PhotoExchangeActorWS}
import consts.RequestTypes.RequestTypes
import consts.RequestTypes

object HandlingActorFactory {

  def getActorProps(requestType: RequestTypes): Props = {
    Logger.info(s"HandlingActorFactory, returning props for $requestType")

    // the _ case is not handled as we'll never get here if the request is not valid
    requestType match {
      case RequestTypes.PHOTO => PhotoExchangeActorWS.props
      case RequestTypes.CONTENT => ContentExchangeActorWS.props
    }
  }
}
