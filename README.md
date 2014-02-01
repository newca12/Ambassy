# Ambassy [![Build Status](https://buildhive.cloudbees.com/job/newca12/job/Ambassy/badge/icon)](https://buildhive.cloudbees.com/job/newca12/job/Ambassy/) [![Ohloh](http://www.ohloh.net/p/Ambassy/widgets/project_thin_badge.gif)](https://www.ohloh.net/p/Ambassy)

## About
Embrace the Unix philosophy, write program that do one thing and do it well.  
And if your programs need something else to be done they should contact their local ambassy.

Ambassy is an EDLA project.

The purpose of [edla.org](http://www.edla.org) is to promote the state of the art in various domains.

## How to use it
* start a redis server
* java -jar target/scala-2.10/Ambassy-assembly-0.1-SNAPSHOT.jar
* http://localhost:8080/addtocache/3 (push element of size 3 and pop required elements to keep cache size constant)

## How to make a runnable single jar
sbt assembly

## TODO
scala-redis : try PoC done for using Akka IO 2.2
https://github.com/debasishg/scala-redis/tree/redis-akka-io  
or  
https://github.com/chrisdinn/brando 

## License
© 2012 Olivier ROLAND. Distributed under the GPLv3 License.
