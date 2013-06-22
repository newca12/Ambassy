package org.edla.ambassy

import scala.collection.mutable.HashMap
import scala.collection.mutable.SynchronizedMap
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

import org.edla.ambassy.protocol.Action
import org.edla.ambassy.protocol.CommandResult
import org.edla.ambassy.protocol.CommandTransco
import org.edla.ambassy.protocol.Profile
import org.edla.ambassy.protocol.Version
import org.edla.ambassy.service.cache.CacheService

import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout.durationToTimeout
import spray.can.Http
import spray.can.server.Stats
import spray.http.ContentType
import spray.http.MediaTypes
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import spray.httpx.marshalling.Marshaller
import spray.routing.Directive.pimpApply
import spray.routing.HttpService
import spray.routing.ValidationRejection
import spray.routing.directives.CachingDirectives.routeCache
import spray.routing.directives.CompletionMagnet.fromObject
import spray.routing.directives.ParamDefMagnet.apply
import spray.util.actorSystem
import spray.util.pimpDuration

import org.edla.ambassy.protocol.CommandProtocol._
//org.edla.ambassy.protocol.CommandProtocol._ needed for implicits but not bring by Organize imports


// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class AmbassyServiceActor extends Actor with AmbassyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

// this trait defines our service behavior independently from the service actor
trait AmbassyService extends HttpService { //TODO simplify with SimpleRoutingApp

  //sample
  val action1 = Action("convert", "-resize 72x72^^  -gravity center -extent 72x72 /tmp/out2.png", "", None, None, None)
  val action2 = Action("convert", "-resize 72x72^^  -gravity center -extent 72x72 /tmp/out2.png", "", None, None, None)
  val profil1: Profile = Profile("id1", "", "", "", List(action1))
  val profil2: Profile = Profile("id2", "", "", "", List(action2))
  var profilesList = List(profil1, profil2)

  //TODO SynchronizedMap or actor ?
  //TODO Use JSON Map to avoid map => list conversion
  var profiles = new HashMap[String, Profile] with SynchronizedMap[String, Profile]
  //val profiles: Map[String, Profile] = Map((profil1.id, profil1), (profil2.id, profil2))
  //val actions: Map[String, Action] = Map((action1.id, action1))

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val route = {
    path("") {
      respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
        complete(index)
      }
    } ~
      path("ping") {
        validate(Boot.cache, "Cache service is disabled")
        complete("PONG!")
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
            entity(as[CommandTransco]) { commandTransco =>
              validate(Boot.cache, "no transco") {
                complete {
                  //TODO check profile existence
                  val profile = profiles.getOrElse(commandTransco.id, profil1)
                  val fileIn = commandTransco.path
                  //TODO implement multiple actions profile
                  val action = profile.actions.head
                  run(action.id + " " + fileIn + " " + action.inOpt)
                }
              }
            })
      } ~
      path("profile" / Segment) { id =>
        get {
          // None is marshalled to an EmptyEntity by default
          // http://stackoverflow.com/a/15357394
          rejectEmptyResponse {
            complete {
              //get is needed to avoid key not found exception and get EmptyEntity
              profiles.get(id)
              //profilesList.find(profile => profile.id == id)
            }
          }
        }
      } ~
      path("profile.json") {
        post(
          entity(as[Profile]) { profile =>
            //complete {  
            profiles.get(profile.id) match {
              case None =>
                profiles += (profile.id -> profile)
                complete {
                  profiles.values.toList
                }
              //https://groups.google.com/forum/#!topic/spray-user/b0-QxKMZAZ0
              //Option 1 will be easier but not allow you to supply a custom error message
              case Some(x) => reject(ValidationRejection("Id: " + profile.id + " is already used by the service"))
              //}
              //http://stackoverflow.com/questions/6998676/converting-a-scala-map-to-a-list
              //val t: List[Profile] = 
              //profiles.view.map { case (k, v) => (k.getBytes, v) } toList

            }
          })
      } ~
      path("profiles.json") {
        get {
          complete(profiles.values.toList)
        } ~
          post(
            entity(as[List[Profile]]) { profile =>
              complete {
                //TODO check for duplication
                profile.foreach { p => profiles = profiles += (p.id -> p) }
                profiles.values.toList
                //profilesList = profile ::: profilesList
                //profilesList
              }
            })
      } ~
      path("sample.json") {
        get {
          complete(profilesList)
        }
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
    path("addtocache" / Segment) {
      elem =>
        //TODO not expected behaviour
        validate(Boot.cache, "Cache service is disabled") {
          get {
            Boot.cacheService ! CacheService.Push(elem)
            complete {
              "Received GET request for addtocache " + elem
            }
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

  //http://stackoverflow.com/a/6013932
  def run(in: String): CommandResult = {
    println(in)
    val qb = Process(in)
    var out = List[String]()
    var err = List[String]()

    val exit = qb ! ProcessLogger((s) => out ::= s, (s) => err ::= s)

    CommandResult(out.reverse, err.reverse, exit)
  }
}