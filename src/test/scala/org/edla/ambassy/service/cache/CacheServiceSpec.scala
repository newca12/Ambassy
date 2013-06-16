package org.edla.ambassy.service.cache

//import org.specs2.mutable.Specification
//import spray.testkit.Specs2RouteTest
import spray.testkit.ScalatestRouteTest
import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers
import spray.http._
import StatusCodes._
import org.edla.ambassy._

class ScalatestRouteTestSpec extends FreeSpec with MustMatchers with ScalatestRouteTest with AmbassyService {
  def actorRefFactory = system

  "The CacheService should" - {

    "return a 'PONG!' response for GET requests to /ping" in {
      Get("/ping") ~> cacheRoute ~> check { entityAs[String] === "PONG!" }
    }
  }

  /* ScalatestRouteTest example
    "a test using a directive and some checks" in {
      val pinkHeader = RawHeader("Fancy", "pink")
      Get() ~> addHeader(pinkHeader) ~> {
        respondWithHeader(pinkHeader) { complete("abc") }
      } ~> check {
        status must be === OK
        body must be === HttpBody(ContentType(`text/plain`, `ISO-8859-1`), "abc")
        header("Fancy") must be === Some(pinkHeader)
      }
    }

    "proper rejection collection" in {
      Post("/abc", "content") ~> {
        (get | put) {
          complete("naah")
        }
      } ~> check {
        rejections must be === List(MethodRejection(GET), MethodRejection(PUT))
      }
    }
  }*/

}
/* specs2 working version
class CacheServiceSpec extends Specification with Specs2RouteTest with CacheService {
  def actorRefFactory = system

  "The CacheService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> cacheRoute ~> check { entityAs[String] must contain("Say hello") }
    }

    "return a 'PONG!' response for GET requests to /ping" in {
      Get("/ping") ~> cacheRoute ~> check { entityAs[String] === "PONG!" }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> cacheRoute ~> check { handled must beFalse }
    }

    //# source-quote (for the documentation site)
    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(cacheRoute) ~> check {
        status === MethodNotAllowed
        entityAs[String] === "HTTP method not allowed, supported methods: GET, POST"
      }
    }
    //#
  }
}
*/