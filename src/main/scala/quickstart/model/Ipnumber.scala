package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Ipnumber(rid: Option[Int], ip: String, installdate:String)

class Ipnumbers(tag: Tag) extends Table[Ipnumber](tag,"ipnumber") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def ip = column[String]("IP")
  def installdate = column[String]("INSTALLDATE")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?,ip , installdate ) <> (Ipnumber.tupled, Ipnumber.unapply)
  def noId = (ip , installdate)
}