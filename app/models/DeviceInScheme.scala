package models

import play.api.libs.json.{Json, JsObject, __}
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._


case class DeviceInScheme(id: Int, x: Int, y: Int)

object DeviceInScheme {
  val ID = "id"
  val X = "x"
  val Y = "y"

  def toJson(deviceInScheme: DeviceInScheme): JsObject = Json.obj(
    ID -> deviceInScheme.id,
    X -> deviceInScheme.x,
    Y -> deviceInScheme.y
  )

  implicit val deviceInSchemeWrites = (
    (__ \ ID).write[Int] and
      (__ \ X).write[Int] and
      (__ \ Y).write[Int]
    )(unlift(DeviceInScheme.unapply))
}

