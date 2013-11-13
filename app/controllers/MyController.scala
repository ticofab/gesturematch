package controllers

import play.api.Logger
import akka.actor.ActorSystem
import actors.{HandlingActorFactory, MatcherActor}
import play.api.mvc.Controller

class MyController extends Controller {
  def isRequestValid(`type`: String, swipeStart: Int, swipeEnd: Int) = {
    // TODO:
    //    check more things about parameters
    //    - api key must be valid
    //    - app id must be valid
    //    - .....

    val validRequestType = HandlingActorFactory.getValidRequests.contains(`type`)
    val differentSwipes = swipeEnd != swipeStart
    validRequestType && differentSwipes
  }
}

object MyController extends Controller {
  Logger.info("\n******* Server starting. Creating ActorSystem. ********")
  val system = ActorSystem("screens-system")
  val matchingActor = system.actorOf(MatcherActor.props)
}
