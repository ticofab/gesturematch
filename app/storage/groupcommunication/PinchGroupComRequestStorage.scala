package storage.groupcommunication

import models.matching.GroupComMatchRequest
import storage.base.ListRequestStorage

/*
 * Storage for pinch match requests meant for inter group communication.
 */
object PinchGroupComRequestStorage {
  private val instance = new ListRequestStorage[GroupComMatchRequest]
  def get = instance
}