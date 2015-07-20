package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Ip(rid: Option[Int],ip: String, name: String)

class Ips(tag: Tag) extends Table[Ip](tag,"ips") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def ip = column[String]("NAME")
  def name = column[String]("ADD1")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, ip , name ) <> (Ip.tupled, Ip.unapply)
  def noId = (ip , name)
}