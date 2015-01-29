/*
 * Copyright 2014-2015 Fabio Tiriticco, Fabway
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

package storage.base

/**
 * implementation of Request Storage using a simple List
 */
class ListRequestStorage[A] extends RequestStorage[A] {
  // This could become a mem cache DB or some other abstraction
  var requests: List[A] = List[A]()

  // interfaces with the storage
  def getRequests = requests

  def addRequest(r: A) = requests = r :: requests

  def skimRequests(skimFilter: (A) => Boolean) = requests = requests.filter(skimFilter)

  def skimRequests(skimFilter: (A, A) => Boolean, r: A) = requests = requests.filter(skimFilter(r, _))
}
