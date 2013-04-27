package org.edla.ambassy

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import org.edla.ambassy.service.cache.CacheService.CacheServiceActor

object Boot extends App {

  // we need an ActorSystem to host our application in
  val cacheSystem = ActorSystem("cache")
  val cacheService = cacheSystem.actorOf(Props[CacheServiceActor], "ambassy-cache-service")

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("ambassy")

  // create and start our service actor
  val service = system.actorOf(Props[AmbassyServiceActor], name = "ambassy-service")

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)

}