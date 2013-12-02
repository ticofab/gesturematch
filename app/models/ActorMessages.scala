package models

// matching stuff
case class NewRequest(request: RequestToMatch)

// client handling actor messages
case class ClientConnected()
case class Matched(myInfo: MatcheeInfo, matcheesInfo: List[MatcheeInfo])

// inter actor messages
sealed trait MatcheeMessage
case class MatcheeDisconnected(reason: Option[String] = None) extends MatcheeMessage
case class MatcheeBrokeConnection(reason: Option[String] = None) extends MatcheeMessage
case class MatcheeDelivers() extends MatcheeMessage



