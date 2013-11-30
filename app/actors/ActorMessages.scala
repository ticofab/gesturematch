package actors

import play.api.libs.iteratee.Concurrent
import models.{MatcheeInfo, RequestToMatch}

// matching stuff
case class NewRequest(request: RequestToMatch)

// request handling actors
case class Setup(channel: Option[Concurrent.Channel[String]])
case class Matched(matcheesInfo: List[MatcheeInfo])
case class Input(input: String)

