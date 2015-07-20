package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Mapping(rid: Option[Int],channel: String, game: String, types:String, directory:String,regdate:String)

class Mappings(tag: Tag) extends Table[Mapping](tag,"mapping") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def channel = column[String]("CHANNEL")
  def game = column[String]("GAME")
  def types = column[String]("TYPE")
  def directory = column[String]("DIRECTORY")
  def regdate = column[String]("REGDATE")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, channel,game , types, directory, regdate ) <> (Mapping.tupled, Mapping.unapply)
  def noId = (channel,game , types, directory, regdate)
}