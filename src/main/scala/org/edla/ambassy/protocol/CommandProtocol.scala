package org.edla.ambassy.protocol

import spray.json.DefaultJsonProtocol
import spray.json.JsonFormat
import spray.json.NullOptions

case class CommandOrder(name: String)
case class Version(name: String)
case class CommandResult(out: List[String], err: List[String], exit: Int)
case class Profile(id: String, sourceFormat: String, targetFormat: String, resolution: String, actions: List[String])
case class Action(id: String, inOpt: String, outOpt: String, globalOpt: Option[String], effectOpt: Option[String], readModifer: Option[String])
case class Profiles(profils: List[Profile])
case class CommandTransco(id: String, path: String)

object CommandProtocol extends DefaultJsonProtocol with NullOptions {
  implicit val commandOrderFormat = jsonFormat1(CommandOrder)
  implicit val versionFormat = jsonFormat1(Version)
  implicit val commandResultFormat = jsonFormat3(CommandResult)
  implicit val profileFormat = jsonFormat5(Profile)
  implicit val actionFormat = jsonFormat6(Action)
  implicit val profilesFormat = jsonFormat1(Profiles)
  implicit val commandTranscoFormat = jsonFormat2(CommandTransco)
}
