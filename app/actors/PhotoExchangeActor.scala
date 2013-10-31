package actors

import akka.actor.{Props, Actor}

class PhotoExchangeActor extends HandlingActor {
  def receive: Actor.Receive = ???
}

object PhotoExchangeActor {
  def props: Props = Props(classOf[PhotoExchangeActor])
}

