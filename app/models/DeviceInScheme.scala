package models

import consts.ScreenPositions.ScreenPosition
import play.api.libs.json.Json

case class DeviceInScheme(position: ScreenPosition, idInGroup: Int)

object DeviceInScheme {
  def toJson(deviceInScheme: DeviceInScheme) = Json.obj(deviceInScheme.position.toString -> deviceInScheme.idInGroup)
}