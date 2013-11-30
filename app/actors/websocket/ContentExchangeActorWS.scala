package actors.websocket

import akka.actor.{Actor, Props}
import play.api.Logger
import consts.Timeouts
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import helpers.JsonResponseHelper
import actors._
import actors.Setup
import models.MatcheeInfo
import actors.Input
import scala.Some

/**
 * This actor will simply pass the content to the actors of the other matched requests.
 */
class ContentExchangeActorWS extends HandlingActorWS {
  def receive: Actor.Receive = {
    case Setup(channel) => {
      this.channel = channel

      // start a timeout to close the connection after a while
      Promise.timeout(channel.foreach(x => x.eofAndEnd()), Timeouts.maxOldestRequestInterval)
    }

    case Matched(groupMatcheesInfo: List[MatcheeInfo]) => {
      {
        val yo = groupMatcheesInfo.partition(x => x.handlingActor == self)
        myInfo = Some(yo._1.head)
        matcheesInfo = Some(yo._2)
      }

      Logger.info(s"$self, Matched message. myInfo: $myInfo, others: $matcheesInfo")

      // TODO: be safer with Options
      val jsonToSend = JsonResponseHelper.getMatchedResponse(myInfo.get, matcheesInfo.get)
      channel.foreach(x => {
        x.push(jsonToSend)
        x.eofAndEnd()
      })
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // don't do anything with it.
    }
  }
}

object ContentExchangeActorWS {
  def props: Props = Props(classOf[ContentExchangeActorWS])
}
