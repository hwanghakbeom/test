package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Game(rid: Option[Int],name: String, companyname: String,companynumber: String, owner: String,add1: String, startdate: String,enddate: String, ratio: String,ratiodetail: String, add2:String)

class Games(tag: Tag) extends Table[Game](tag,"game") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column, 
  def name = column[String]("NAME")
  def companyname = column[String]("COMPANYNAME")
  def companynumber = column[String]("COMPANYNUMBER")
  def owner = column[String]("OWNER")
  def add1 = column[String]("ADD1")
  def startdate = column[String]("STARTDATE")
  def enddate = column[String]("ENDDATE")
  def ratio = column[String]("RATIO")
  def ratiodetail = column[String]("RATIODETAIL")
  def add2 = column[String]("ADD2")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, name , companyname, companynumber, owner, add1, startdate, enddate, ratio, ratiodetail, add2 ) <> (Game.tupled, Game.unapply)
  def noId = (name , companyname, companynumber, owner, add1, startdate, enddate, ratio, ratiodetail, add2)
}