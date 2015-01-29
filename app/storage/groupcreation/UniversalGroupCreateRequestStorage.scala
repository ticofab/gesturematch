package storage.groupcreation

import models.matching.GroupCreateMatchRequest
import storage.base.ListRequestStorage

/*
 * Storage for requests which use the Universal match criteria meant for group creation.
 */
object UniversalGroupCreateRequestStorage {
  private val instance = new ListRequestStorage[GroupCreateMatchRequest]
  def get = instance
}