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

package helpers.logging

import models.matching.base.MatchRequest

object LoggingHelper {
  def getNewRequestLogging(name: String, request: MatchRequest, possiblyMatchingRequests: Int, msg: String = "") = {
    s"$name, new ${request.toString}, matching it with $possiblyMatchingRequests existing requests --> $msg"
  }
}
