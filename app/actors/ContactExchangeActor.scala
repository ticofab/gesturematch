package actors

import akka.actor.{Props, Actor}

class ContactExchangeActor extends Actor {
  def receive: Actor.Receive = ???
}

object ContactExchangeActor {
  def props: Props = Props(classOf[ContactExchangeActor])
}