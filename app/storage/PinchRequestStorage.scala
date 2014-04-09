/*
 * Copyright 2014 Fabio Tiriticco, Fabway
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

package storage

import models.RequestToMatch

object PinchRequestStorage extends RequestStorage {

  // This could become a mem cache DB or some other abstraction
  private var requests: List[RequestToMatch] = List()

  // interfaces with the storage
  def getRequests = requests

  def addRequest(r: RequestToMatch) = requests = r :: requests

  def skimRequests(skimFilter: RequestToMatch => Boolean) = requests = requests.filter(skimFilter)

  def skimRequests(skimFilter: (RequestToMatch, RequestToMatch) => Boolean, r: RequestToMatch) =
    requests = requests.filter(skimFilter(r, _))
}
