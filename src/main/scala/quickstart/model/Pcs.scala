package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Pc(rid: Option[Int],name: String, add1: String, add2: String, add3: String, owner: String,phone: String, mobile: String, channel: String, lastdate: String, isfinished: String,regdate:String)

class Pcs(tag: Tag) extends Table[Pc](tag,"pcs") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def name = column[String]("NAME")
  def add1 = column[String]("ADD1")
  def add2 = column[String]("ADD2")
  def add3  = column[String]("ADD3")
  def owner = column[String]("OWNER")
  def phone = column[String]("PHONE")
  def mobile = column[String]("MOBILE")
  def channel = column[String]("CHANNEL")
  def lastdate = column[String]("LASTDATE")
  def isfinished = column[String]("ISFINISHED")
  def regdate = column[String]("regdate")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, name , add1 , add2, add3, owner, phone, mobile, channel, lastdate, isfinished,regdate ) <> (Pc.tupled, Pc.unapply)
  def noId = (name , add1 , add2, add3, owner, phone, mobile, channel, lastdate, isfinished,regdate)
}