package org.edla.ambassy.service.cache

import akka.actor.Actor
import akka.actor.ActorLogging
import com.redis._

object CacheService {
  //22711
  val r = new RedisClient("localhost", 6379)
  val maxCache = 10
  var currentCache = 0

  case class Push(elem: String)

  class CacheServiceActor extends Actor with ActorLogging {
    def receive = {
      case Push(elem) ⇒ {
        log.info(currentCache + " Push " + elem)
        val in = elem.split(":")
        if (in(0).toInt <= maxCache) {
          currentCache = currentCache + in(0).toInt
          var toFree = currentCache - maxCache
          while (toFree > 0) {
            val out = r.rpop("fifo") match {
              case Some(foo) => foo.split(":")
              case None => "0: ".split(":")
            }
            log.info(currentCache + " Pop " + out(1))
            currentCache = currentCache - out(0).toInt
            toFree = toFree - out(0).toInt
          }
          r.lpush("fifo", elem)
        }
      }
    }
  }

}