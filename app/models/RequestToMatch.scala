package models

import akka.actor.ActorRef

/**
 * Created with IntelliJ IDEA.
 * User: fabiotiriticco
 * Date: 28/10/13
 * Time: 17:53
 */
class RequestToMatch(val latitude: Double,
                     val longitude: Double,
                     val timestamp: Long,
                     val swipeStart: Int,
                     val swipeEnd: Int,
                     val equalityParam: String,
                     val payload: String,
                     val handlingActor: ActorRef)
