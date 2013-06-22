package org.edla.ambassy.service

import org.edla.ambassy.AmbassyService
import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers

import spray.testkit.ScalatestRouteTest

class AmbassyServiceSpec extends FreeSpec with MustMatchers with ScalatestRouteTest with AmbassyService {
  def actorRefFactory = system

  "The AmbassyService should" - {

    "return a 'PONG!' response for GET requests to /ping" in {
      Get("/ping") ~> route ~> check { entityAs[String] === "PONG!" }
    }

    //TODO Request was not handled
/*    "do proper rejection for unknown id" in {
      Get("/profile/id0") ~> route ~> check {
        entityAs[String] === "HTTP method not allowed, supported methods: GET, POST"
      }
    }*/
    
  }
}
