package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Channel(rid: Option[Int],name: String, company:String,regnumber: String, owner:String,address: String, regdate:String,enddate:String,user:String)

class Channels(tag: Tag) extends Table[Channel](tag,"channel") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def name = column[String]("NAME")
  def company = column[String]("COMPANY")
  def regnumber = column[String]("REGNUMBER")
  def owner = column[String]("OWNER")
  def address = column[String]("ADDRESS")
  def regdate = column[String]("REGDATE")
  def enddate = column[String]("ENDDATE")
  def user = column[String]("USER")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, name, company,regnumber,owner,address,regdate,enddate,user ) <> (Channel.tupled, Channel.unapply)
  def noId = (name, company,regnumber,owner,address,regdate,enddate,user )
}