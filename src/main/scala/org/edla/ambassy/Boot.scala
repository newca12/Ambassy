package org.edla.ambassy

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import org.edla.ambassy.service.cache.CacheService.CacheServiceActor
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File

object Boot extends App {

  //TODO we wan't maximum similarities with dropwizard 
  val usage = """
usage: java -jar ambassy_2.10-0.1-SNAPSHOT-jar-with-dependencies.jar
  [-h] [-v] {server} ...

positional arguments:
  {server}               available commands

optional arguments:
  -h, --help             show this help message and exit
  -v, --version          show the service version and exit
  """

  //http://stackoverflow.com/questions/2315912/scala-best-way-to-parse-command-line-parameters-cli  
  if (args.length == 0) println(usage)
  val arglist = args.toList
  type OptionMap = Map[Symbol, Any]

  def nextOption(map: OptionMap, list: List[String]): OptionMap = {
    def isSwitch(s: String) = (s(0) == '-')
    list match {
      case Nil => map
      case "--max-size" :: value :: tail =>
        nextOption(map ++ Map('maxsize -> value.toInt), tail)
      case "--min-size" :: value :: tail =>
        nextOption(map ++ Map('minsize -> value.toInt), tail)
      case string :: opt2 :: tail if isSwitch(opt2) =>
        nextOption(map ++ Map('infile -> string), list.tail)
      case string :: Nil => nextOption(map ++ Map('infile -> string), list.tail)
      case option :: tail =>
        println("Unknown option " + option)
        println(usage)
        sys.exit(1)
    }
  }
  val options = nextOption(Map(), arglist)
  println(options)
  
  val conf = ConfigFactory.parseFile(new File("./application.conf"))
  val cache = conf.getBoolean("ambassy-services.cache");
  println("cache service : " + cache)

  // we need an ActorSystem to host our application in
  val cacheSystem = ActorSystem("cache")
  val cacheService = cacheSystem.actorOf(Props[CacheServiceActor], "ambassy-cache-service")

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("ambassy")

  // create and start our service actor
  val service = system.actorOf(Props[AmbassyServiceActor], name = "ambassy-service")

  IO(Http) ! Http.Bind(service, "localhost", port = 8080)

}