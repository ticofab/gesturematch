package actors

import akka.actor.{Props, Actor}

/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 15:47
 */

class ContactExchangeActor extends Actor {
  def receive: Actor.Receive = ???
}

object ContactExchangeActor {
  def props: Props = Props(classOf[ContactExchangeActor])
}