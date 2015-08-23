package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Ip(rid: Option[Int],pcsid: String, ip: String, installdate:String)

class Ips(tag: Tag) extends Table[Ip](tag,"ips") {
  def rid = column[Int]("RID") // This is the primary key column
  def pcsid = column[String]("PCSID")
  def ip = column[String]("IP",O.PrimaryKey)
  def installdate = column[String]("INSTALLDATE")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, pcsid,ip , installdate ) <> (Ip.tupled, Ip.unapply)
  def noId = (pcsid,ip , installdate)
}