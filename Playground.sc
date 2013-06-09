
import scala.sys.process._
import scala.concurrent.Future
import scala.concurrent.future
import concurrent.ExecutionContext.Implicits.global

/////
object Playground {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val cmd0 = "ls -l /tmp"                         //> cmd0  : String = ls -l /tmp
  val cmd1 = "sleep 3"                            //> cmd1  : String = sleep 3
  val p = Process(cmd1)                           //> p  : scala.sys.process.ProcessBuilder = [sleep, 3]
  var normalLines = 0                             //> normalLines  : Int = 0
  var errorLines = 0                              //> errorLines  : Int = 0
  val countLogger = ProcessLogger(line => normalLines += 1,
    line => errorLines += 1)                      //> countLogger  : scala.sys.process.ProcessLogger = scala.sys.process.ProcessLo
                                                  //| gger$$anon$1@38c5a1ba

  println("start")                                //> start

  val f: Future[Int] = future {
    println("start1")
    val exit = p ! countLogger
    println("start2")
    exit
  }                                               //> f  : scala.concurrent.Future[Int] = scala.concurrent.impl.Promise$DefaultPro
                                                  //| mise@62725296
  f onSuccess {
    case msg => println("result:" + msg)
    //case _ => println("other")
  }                                               //> start1

  f onFailure {
    case _ => println("failure")
  }
  
  f onComplete {
    case res => println("complete:"+res)
  }
  
  normalLines                                     //> res0: Int = 0
  errorLines                                      //> res1: Int = 0
  println("end")                                  //> end
  Thread.sleep(4000)                              //> start2
                                                  //| complete:Success(0)
                                                  //| result:0|
}