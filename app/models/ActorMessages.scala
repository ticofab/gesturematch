package models

// matching stuff
case class NewRequest(request: RequestToMatch)

// client handling actor messages
case class ClientConnected(remoteAddress: String)
case class Matched(myInfo: Matchee, matcheesInfo: List[Matchee])

// inter actor messages
sealed trait MatcheeMessage
case class MatcheeLeftGroup(matchee: Matchee, reason: Option[String] = None) extends MatcheeMessage
case class MatcheeDelivers(matchee: Matchee, payload: String) extends MatcheeMessage



