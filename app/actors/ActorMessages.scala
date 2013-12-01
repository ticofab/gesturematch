package actors

import play.api.libs.iteratee.Concurrent
import models.{MatcheeInfo, RequestToMatch}

// matching stuff
case class NewRequest(request: RequestToMatch)

// client handling actor messages
case class Setup(channel: Option[Concurrent.Channel[String]])
case class Matched(myInfo: MatcheeInfo, matcheesInfo: List[MatcheeInfo])
case class Input(input: String)

// inter actor messages
sealed trait MatcheeMessage
case class MatcheeDisconnected(reason: Option[String] = None) extends MatcheeMessage
case class MatcheeBrokeConnection(reason: Option[String] = None) extends MatcheeMessage
case class MatcheeDelivers() extends MatcheeMessage



