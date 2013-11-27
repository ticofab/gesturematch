package helpers

import models.RequestToMatch
import actors.HandlingActorFactory

object RequestAnalyticsHelper {
  def requestIsValid(`type`: String, swipeStart: Int, swipeEnd: Int) = {
    // TODO:
    //    check more things about parameters
    //    - api key must be valid
    //    - app id must be valid
    //    - .....

    val validRequestType = HandlingActorFactory.getValidRequestsTypes.contains(`type`)
    val differentSwipes = swipeEnd != swipeStart
    validRequestType && differentSwipes
  }

  def requestsAreCompatible(r1: RequestToMatch, r2: RequestToMatch): Boolean = {
    // check on latitude
    val latDiff: Double = scala.math.pow(r1.latitude - r2.latitude, 2)
    val lonDiff: Double = scala.math.pow(r1.longitude - r2.longitude, 2)
    val closeEnough: Boolean = latDiff < 0.01 && lonDiff < 0.01

    // check if equality parameters are the same
    val equality = r1.equalityParam == r2.equalityParam

    // check if apiKey is the same
    val sameApiKey = r1.apiKey == r2.apiKey

    // check if appId is the same
    val sameAppId = r1.appId == r2.appId

    closeEnough && equality && sameApiKey && sameAppId
  }
}
