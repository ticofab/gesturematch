package actors

import akka.actor.{Props, Actor}


/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 16:05
 */
class PhotoExchangeActor extends HandlingActor {
  def receive: Actor.Receive = ???
}

object PhotoExchangeActor {
  def props: Props = Props(classOf[PhotoExchangeActor])
}

