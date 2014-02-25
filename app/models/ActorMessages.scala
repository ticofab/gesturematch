package models

// matching stuff
case class NewRequest(request: RequestToMatch)

// client handling actor messages
case class ConnectedClient(remoteAddress: String, apiKey: String, appId: String, os: String, deviceId: String)
case class Matched(myInfo: Matchee, matcheesInfo: List[Matchee], groupId: String, scheme: Option[List[DeviceInScheme]] = None)

// inter actor messages
sealed trait MatcheeMessage
case class MatcheeLeftGroup(matchee: Matchee, reason: Option[String] = None) extends MatcheeMessage
case class MatcheeDelivers(matchee: Matchee, delivery: Delivery) extends MatcheeMessage



