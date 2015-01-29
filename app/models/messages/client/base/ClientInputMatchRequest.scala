package models.messages.client.base

abstract class ClientInputMatchRequest(val criteria: String,
                                       val areaStart: String,
                                       val areaEnd: String,
                                       val equalityParam: Option[String])
