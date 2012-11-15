package org.edla.ambassy.service.cache

import akka.actor.Actor
import akka.actor.ActorLogging
import com.redis._

object CacheService {
  
  val r = new RedisClient("localhost", 6379)
  
  case class Push(elem: String)

  class CacheServiceActor extends Actor with ActorLogging {
    def receive = {
      case Push(elem) ⇒ {
        log.info("Push " + elem)
        r.lpush("fifo", elem)
      }
    }
  }

}