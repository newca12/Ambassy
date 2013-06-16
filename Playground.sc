
import scala.sys.process._
import scala.concurrent.Future
import scala.concurrent.future
import concurrent.ExecutionContext.Implicits.global

/////
object Playground {
def run(in: String): (List[String], List[String], Int) = {
  val qb = Process(in)
  var out = List[String]()
  var err = List[String]()

  val exit = qb ! ProcessLogger((s) => out ::= s, (s) => err ::= s)

  (out.reverse, err.reverse, exit)
}                                                 //> run: (in: String)(List[String], List[String], Int)
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val cmd0 = "ls -l /tmp/"                        //> cmd0  : String = ls -l /tmp/
  val cmd1 = "sleep 3"                            //> cmd1  : String = sleep 3
  val p = Process(cmd0)                           //> p  : scala.sys.process.ProcessBuilder = [ls, -l, /tmp/]
  var normalLines = 0                             //> normalLines  : Int = 0
  var errorLines = 0                              //> errorLines  : Int = 0
  val countLogger = ProcessLogger(line => normalLines += 1,
    line => errorLines += 1)                      //> countLogger  : scala.sys.process.ProcessLogger = scala.sys.process.ProcessLo
                                                  //| gger$$anon$1@131b4c5d

  println("start")                                //> start
 	 run("ls -l /tmp")                        //> res0: (List[String], List[String], Int) = (List(lrwxr-xr-x@ 1 root  wheel  1
                                                  //| 1 Jul 17  2010 /tmp -> private/tmp),List(),0)

  val f: Future[Int] = future {
    println("start1")
    val exit = p ! countLogger
    println("start2:"+exit)
    exit
  }                                               //> start1
                                                  //| f  : scala.concurrent.Future[Int] = scala.concurrent.impl.Promise$DefaultPro
                                                  //| mise@4ba43077
  f onSuccess {
    case msg => println("result:" + msg)
    //case _ => println("other")
  }                                               //> start2:0

  f onFailure {
    case _ => println("failure")
  }                                               //> result:0
  
  f onComplete {
    case res => println("complete:"+res)
  }

  Thread.sleep(1000)                              //> complete:Success(0)
  normalLines                                     //> res1: Int = 8
  errorLines                                      //> res2: Int = 0
  println("end")                                  //> end
}