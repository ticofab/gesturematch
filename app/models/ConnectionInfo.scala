package models

import models.ScreenPosition.ScreenPosition

/**
 * This object holds information about the entire connection. Each entity with this object
 * knows its relative position and the number of devices in the entire connection.
 */
class ConnectionInfo(val devicesInConnection: Int,
                     val devicePosition: ScreenPosition)
