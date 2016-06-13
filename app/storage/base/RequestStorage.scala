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

package storage.base

trait RequestStorage[A] {

  // interfaces with the storage
  def getRequests: List[A]

  def addRequest(r: A)

  def skimRequests(skimFilter: A => Boolean)

  def skimRequests(skimFilter: (A, A) => Boolean, r: A)
}
