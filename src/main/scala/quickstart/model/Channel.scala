package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Channel(rid: Option[Int],name: String, user:String)

class Channels(tag: Tag) extends Table[Channel](tag,"channel") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def name = column[String]("NAME")
  def user = column[String]("USER")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, name, user ) <> (Channel.tupled, Channel.unapply)
  def noId = (name, user )
}