package controllers

import play.api.Logger
import play.api.Play.current
import actors.{TouchMatchingActor, PositionMatcherActor}
import play.api.mvc.Controller
import play.api.libs.concurrent.Akka

class MyController extends Controller {

}

object MyController extends Controller {
  Logger.info("\n******* Server starting. Creating ActorSystem. ********")
  val positionMatchingActor = Akka.system.actorOf(PositionMatcherActor.props)
  val touchMatchingActor = Akka.system.actorOf(TouchMatchingActor.props)
}
