package models

case class MatchingGroup(val devicesInGroup: Int, val requests: List[RequestToMatch])
