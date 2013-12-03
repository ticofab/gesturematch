package models

// matching stuff
case class NewRequest(request: RequestToMatch)

// client handling actor messages
case class ClientConnected(remoteAddress: String)
case class Matched(myInfo: MatcheeInfo, matcheesInfo: List[MatcheeInfo])

// inter actor messages
sealed trait MatcheeMessage
case class MatcheeDisconnected(matchee: Option[MatcheeInfo], reason: Option[String] = None) extends MatcheeMessage
case class MatcheeBrokeConnection(matchee: Option[MatcheeInfo], reason: Option[String] = None) extends MatcheeMessage
case class MatcheeDelivers(matchee: Option[MatcheeInfo], payload: String) extends MatcheeMessage



