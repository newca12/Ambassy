package org.edla.ambassy.protocol

import spray.json.DefaultJsonProtocol
import spray.json.NullOptions

case class Version(name: String)
case class CommandResult(out: List[String], err: List[String], exit: Int, path:String)
case class Profile(id: String, sourceFormat: String, targetFormat: String, resolution: String, actions: List[Action])
case class Action(id: String, inOpt: String, outOpt: String, globalOpt: Option[String], effectOpt: Option[String], readModifer: Option[String])
case class CommandTransco(id: String, path: String)

object CommandProtocol extends DefaultJsonProtocol with NullOptions {
  implicit val versionFormat = jsonFormat1(Version)
  implicit val commandResultFormat = jsonFormat4(CommandResult)
  //order does matter this implicit need to be defined before profile implicit
  implicit val actionFormat = jsonFormat6(Action)
  implicit val profileFormat = jsonFormat5(Profile)
  implicit val commandTranscoFormat = jsonFormat2(CommandTransco)
}
