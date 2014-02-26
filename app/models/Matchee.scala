package models

import akka.actor.ActorRef

case class Matchee(handlingActor: ActorRef, idInGroup: Int)
