package injection

import actors.{PinchMatcherActor, SwipeMatcherActor, UniversalMatcherActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Created by fabiotiriticco on 13/06/2016.
  */
class InjectionModule extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[SwipeMatcherActor](InjectionModule.SWIPE_MATCHING_ACTOR_NAME)
    bindActor[PinchMatcherActor](InjectionModule.PINCH_MATCHING_ACTOR_NAME)
    bindActor[UniversalMatcherActor](InjectionModule.UNIVERSAL_MATCHING_ACTOR_NAME)
  }
}

object InjectionModule {
  // aggregate managers
  final val SWIPE_MATCHING_ACTOR_NAME = "swipe_matching_actor"
  final val PINCH_MATCHING_ACTOR_NAME = "pinch_matching_actor"
  final val UNIVERSAL_MATCHING_ACTOR_NAME = "universal_matching_actor"
}
