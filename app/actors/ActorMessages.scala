package actors

import play.api.libs.iteratee.Concurrent
import consts.ScreenPositions.ScreenPosition
import models.RequestToMatch
import consts.MatcheeInfo

// matching stuff
case class NewRequest(request: RequestToMatch)

// request handling actors
case class Setup(channel: Option[Concurrent.Channel[String]])
case class MatchedPosition(position: ScreenPosition, payload: String, othersInfo: List[MatcheeInfo])
case class MatchedGroup(group: List[MatcheeInfo])
case class Input(input: String)

