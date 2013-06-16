package org.edla.ambassy

import java.io.File
import org.parboiled.common.FileUtils
import scala.concurrent.duration._
import akka.actor.{ Props, Actor }
import akka.pattern.ask
import spray.routing.{ HttpService, RequestContext }
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import spray.http.ContentType._
import MediaTypes._
import CachingDirectives._
import org.edla.ambassy.service.cache.CacheService
import spray.json.DefaultJsonProtocol
import scala.concurrent.Future
import spray.json._
import DefaultJsonProtocol._
import org.edla.ambassy.protocol._
import org.edla.ambassy.protocol.Version
import org.edla.ambassy.protocol.CommandProtocol._
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport._
import spray.routing.SimpleRoutingApp
import shapeless.get
import scala.sys.process._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class AmbassyServiceActor extends Actor with AmbassyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(cacheRoute)
}

// this trait defines our service behavior independently from the service actor
trait AmbassyService extends HttpService { //TODO simplify with SimpleRoutingApp

  case class TranscoQuery(source: String, profil: Int)

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat2(TranscoQuery)
  }

  import MyJsonProtocol._

  //val jsonTranscoQuery = TranscoQuery("/tmp/a", 1).toJson
  //val transcoQuery = jsonTranscoQuery.convertTo[TranscoQuery]
  //jsonTranscoQuery.prettyPrint

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val cacheRoute = {
    path("") {
      respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
        complete(index)
      }
    } ~
      path("version") {
        get {
          complete {
            "0.1"
          }
        }
      } ~
      path("version.json") {
        get {
          complete {
            Version("0.1")
          }
        }
      } ~
      path("command.json") {
        get {
          complete("test")
        } ~
          post(
            entity(as[CommandOrder]) { commandOrder =>
              complete {
                run(commandOrder.name)
              }
            })
      } ~
      path("stats") {
        complete {
          actorRefFactory.actorFor("/user/IO-HTTP/listener-0")
            .ask(Http.GetStats)(1.second)
            .mapTo[Stats]
        }
      } ~
      path("timeout") { ctx =>
        // we simply let the request drop to provoke a timeout
      } ~
      path("crash") { ctx =>
        sys.error("crash boom bang")
      } ~
      path("fail") {
        failWith(new RuntimeException("aaaahhh"))
      }
  } ~
    (post | parameter('method ! "post")) {
      path("stop") {
        complete {
          in(1.second) { actorSystem.shutdown() }
          "Shutting down in 1 second..."
        }
      }
    } ~
    //http://localhost:8080/addtocache/x:file
    path("addtocache" / Segment) { elem =>
      get {
        Boot.cacheService ! CacheService.Push(elem)
        complete {
          "Received GET request for addtocache " + elem
        }
      }
    }

  lazy val simpleRouteCache = routeCache()

  lazy val index =
    <html>
      <body>
        <h1>Welcome to <i>Ambassy</i> !</h1>
        <p>Defined resources:</p>
        <ul>
          <li><a href="/ping">/ping</a></li>
          <li><a href="/stats">/stats</a></li>
          <li><a href="/timeout">/timeout</a></li>
          <li><a href="/cached">/cached</a></li>
          <li><a href="/crash">/crash</a></li>
          <li><a href="/fail">/fail</a></li>
          <li><a href="/stop?method=post">/stop</a></li>
        </ul>
      </body>
    </html>

  implicit val statsMarshaller: Marshaller[Stats] =
    //what is the problem with the syntax : ContentTypes.`text/plain` ?
    //by the way replaced thanks to this :
    //val `text/plain` = ContentType(MediaTypes.`text/plain`)
    Marshaller.delegate[Stats, String](ContentType(MediaTypes.`text/plain`)) { stats =>
      "Uptime                : " + stats.uptime.formatHMS + '\n' +
        "Total requests        : " + stats.totalRequests + '\n' +
        "Open requests         : " + stats.openRequests + '\n' +
        "Max open requests     : " + stats.maxOpenRequests + '\n' +
        "Total connections     : " + stats.totalConnections + '\n' +
        "Open connections      : " + stats.openConnections + '\n' +
        "Max open connections  : " + stats.maxOpenConnections + '\n' +
        "Requests timed out    : " + stats.requestTimeouts + '\n'
    }

  def in[U](duration: FiniteDuration)(body: => U): Unit =
    actorSystem.scheduler.scheduleOnce(duration)(body)

  def run(in: String): CommandResult = {
    val qb = Process(in)
    var out = List[String]()
    var err = List[String]()

    val exit = qb ! ProcessLogger((s) => out ::= s, (s) => err ::= s)

    CommandResult(out.reverse, err.reverse, exit)
  }
}