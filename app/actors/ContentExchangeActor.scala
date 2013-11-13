package actors

import akka.actor.{Actor, Props}
import play.api.Logger
import play.api.libs.json.Json
import consts.JsonLabels

/**
 * This actor will simply pass the content to the actors of the other matched requests.
 */
class ContentExchangeActor extends HandlingActor {
  def receive: Actor.Receive = {
    case Setup(channel) => {
      this.channel = channel
    }

    case Matched2(myPosition, myPayload, otherInfo) => {
      Logger.info(s"$self, Matched2() message: $myPosition\n  my payload: $myPayload\n  other's: ${otherInfo._2}")

      val jsonToSend = Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED2,
        JsonLabels.PAYLOAD -> otherInfo._2
      )
      channel.foreach(x => {
        x.push(Json.stringify(jsonToSend))
        x.eofAndEnd()
      })
    }

    case Matched4(myPosition, myPayload, otherPayloads) => {
      Logger.info(s"$self, Matched4() message: $myPosition\n  my payload: $myPayload")

      val jsonToSend = Json.obj(
        JsonLabels.OUTCOME -> JsonLabels.OUTCOME_MATCHED4,
        JsonLabels.PAYLOAD -> Json.arr(
          Json.obj(
            JsonLabels.FIRST_DEVICE -> otherPayloads(0)._2,
            JsonLabels.SECOND_DEVICE -> otherPayloads(1)._2,
            JsonLabels.THIRD_DEVICE -> otherPayloads(2)._2
          )
        )
      )
      channel.foreach(x => {
        x.push(Json.stringify(jsonToSend))
        x.eofAndEnd()
      })
    }

    case Input(input) => {
      Logger.info(s"$self, Input() message: $input")
      // don't do anything with it.
    }
  }
}

object ContentExchangeActor {
  def props: Props = Props(classOf[ContentExchangeActor])
}
