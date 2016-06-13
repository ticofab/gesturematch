/*
 * Copyright 2014-2016 Fabio Tiriticco, Fabway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.scheme

import consts.ScreenPositions
import consts.ScreenPositions.ScreenPosition
import play.api.libs.json.Json

class Scheme() {
  var width: Int = 0
  var height: Int = 0
  var devices: List[DeviceInScheme] = Nil

  private def getDeviceFromId(id: Int) = devices.filter(x => x.id == id).head

  def addFirstDevice() = {
    // first device
    width = 1
    height = 1
    devices = devices :+ new DeviceInScheme(0, 0, 0)
    0
  }

  def addDevice(position: ScreenPosition, relativeToDeviceId: Int): Int = {

    // adding a device
    var d = getDeviceFromId(relativeToDeviceId)

    // do I need to shift all the matrix?
    position match {
      case ScreenPositions.Top => if (d.y - 1 < 0) {
        devices = devices.map(d => new DeviceInScheme(d.id, d.x, d.y + 1))
        d = getDeviceFromId(relativeToDeviceId)
      }
      case ScreenPositions.Left => if (d.x - 1 < 0) {
        devices = devices.map(d => new DeviceInScheme(d.id, d.x + 1, d.y))
        d = getDeviceFromId(relativeToDeviceId)
      }
      case _ => // do nothing
    }

    val newId = devices.length
    val (newX, newY) = position match {
      case ScreenPositions.Top => (d.x, d.y - 1)
      case ScreenPositions.Bottom => (d.x, d.y + 1)
      case ScreenPositions.Left => (d.x - 1, d.y)
      case ScreenPositions.Right => (d.x + 1, d.y)
      case _ => (-1, -1) // error!
    }

    devices = devices :+ new DeviceInScheme(newId, newX, newY)
    width = devices.maxBy(d => d.x).x + 1
    height = devices.maxBy(d => d.y).y + 1

    newId
  }

  def addNextDevice(position: ScreenPosition, newId: Int): Unit = {

    if (position == ScreenPositions.Start) {
      // first device
      width = 1
      height = 1
      devices = devices :+ new DeviceInScheme(newId, 0, 0)
    } else {

      // adding a device
      var d = getDeviceFromId(devices.length - 1)

      // do I need to shift all the matrix?
      position match {
        case ScreenPositions.Top => if (d.y - 1 > 0) {
          devices = devices.map(d => new DeviceInScheme(d.id, d.x, d.y + 1))
          d = getDeviceFromId(devices.length - 1)
        }
        case ScreenPositions.Left => if (d.x - 1 > 0) {
          devices = devices.map(d => new DeviceInScheme(d.id, d.x + 1, d.y))
          d = getDeviceFromId(devices.length - 1)
        }
        case _ => // do nothing
      }

      val (newX, newY) = position match {
        case ScreenPositions.Top => (d.x, d.y - 1)
        case ScreenPositions.Bottom => (d.x, d.y + 1)
        case ScreenPositions.Left => (d.x - 1, d.y)
        case ScreenPositions.Right => (d.x + 1, d.y)
        case _ => (-1, -1) // error!
      }

      devices = devices :+ new DeviceInScheme(newId, newX, newY)
      width = devices.maxBy(d => d.x).x + 1
      height = devices.maxBy(d => d.y).y + 1
    }
  }
}

object Scheme {
  val DEVICES = "devices"
  val WIDTH = "width"
  val HEIGHT = "height"

  def toJson(scheme: Scheme) = Json.obj(
    WIDTH -> scheme.width,
    HEIGHT -> scheme.height,
    DEVICES -> Json.toJson(scheme.devices)
  )
}
