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
    case Setup(c) => {
      this.channel = c

      // start a timeout to close the connection after a while
      Promise.timeout(channel.foreach(x => x.eofAndEnd()), Timeouts.maxOldestRequestInterval)
    }

    case Matched(info: MatcheeInfo, othersInfo: List[MatcheeInfo]) => {
      myInfo = Some(info)
      matcheesInfo = Some(othersInfo)

      Logger.info(s"$self, MatchedDetail message. myInfo: $myInfo, others: $matcheesInfo")

      sendToClient()
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // don't do anything with it.... for now
    }
  }

  def sendToClient() = {
    if (!myInfo.isEmpty && !matcheesInfo.isEmpty) {
      val jsonToSend = JsonResponseHelper.getMatchedResponse(myInfo.get, matcheesInfo.get)
      channel.foreach(x => {
        x.push(jsonToSend)
        x.eofAndEnd() // TODO: get rid of this
      })
    }
  }
}

object ContentExchangeActorWS {
  def props: Props = Props(classOf[ContentExchangeActorWS])
}
