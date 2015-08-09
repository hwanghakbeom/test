package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Dummyexcel(rid: Option[Int],name: String, address: String, startip:String, endip:String)

class Dummyexcels(tag: Tag) extends Table[Dummyexcel](tag,"dummyexcel") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def name = column[String]("NAME")
  def address = column[String]("ADDRESS")
  def startip = column[String]("STARTIP")
  def endip = column[String]("ENDIP")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, name,address , startip, endip ) <> (Dummyexcel.tupled, Dummyexcel.unapply)
  def noId = ( name,address , startip, endip)
}