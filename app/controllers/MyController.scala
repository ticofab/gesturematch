package controllers

import play.api.Logger
import play.api.Play.current
import actors.PositionMatcherActor
import play.api.mvc.Controller
import play.api.libs.concurrent.Akka

class MyController extends Controller {

}

object MyController extends Controller {
  Logger.info("\n******* Server starting. Creating ActorSystem. ********")
  val matchingActor = Akka.system.actorOf(PositionMatcherActor.props)
}
