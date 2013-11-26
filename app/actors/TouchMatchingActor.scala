package actors

import akka.actor.{Actor, Props}

class TouchMatchingActor extends Actor {


  def receive: Actor.Receive = ???
}

object TouchMatchingActor {
  val props = Props(classOf[TouchMatchingActor])
}
