package actors.websocket

import akka.actor.{Props, Actor}

class PhotoExchangeActorWS extends HandlingActorWS {
  def receive: Actor.Receive = ???
}

object PhotoExchangeActorWS {
  def props: Props = Props(classOf[PhotoExchangeActorWS])
}

