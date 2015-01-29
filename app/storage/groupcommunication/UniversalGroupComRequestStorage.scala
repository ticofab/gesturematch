package storage.groupcommunication

import models.matching.GroupComMatchRequest
import storage.base.ListRequestStorage

/*
 * Storage for universal match requests meant for inter group communication.
 */
object UniversalGroupComRequestStorage {
  private val instance = new ListRequestStorage[GroupComMatchRequest]
  def get = instance
}