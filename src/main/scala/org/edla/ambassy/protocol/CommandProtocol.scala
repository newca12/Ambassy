package org.edla.ambassy.protocol

import spray.json.DefaultJsonProtocol
import spray.json.JsonFormat
import spray.json.NullOptions

case class CommandOrder(name : String)
case class Version(name : String)
case class CommandResult(out: List[String], err:List[String], exit: Int)

object CommandProtocol extends DefaultJsonProtocol with NullOptions {
	implicit val commandOrderFormat = jsonFormat1(CommandOrder)
	implicit val versionFormat = jsonFormat1(Version)
	implicit val commandResultFormat = jsonFormat3(CommandResult)
}