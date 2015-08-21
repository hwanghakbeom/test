package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Ipandsite(rid: Option[Int], ip: String, site:String)

class Ipandsites(tag: Tag) extends Table[Ipandsite](tag,"ipsite") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def ip = column[String]("IP")
  def site = column[String]("SITE")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?,ip , site ) <> (Ipandsite.tupled, Ipandsite.unapply)
  def noId = (ip , site)
}