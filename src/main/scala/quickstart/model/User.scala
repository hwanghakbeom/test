package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class User(rid: Option[Int],userid: String, pass: String, name: String, position: String, company: String, email: String, phone: String, mobile: String, work: String,role: String, lastconnect: String)

class Users(tag: Tag) extends Table[User](tag,"users") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def userid = column[String]("USERID")
  def pass = column[String]("PASS")
  def name = column[String]("NAME")
  def position  = column[String]("POSITION")
  def company = column[String]("COMPANY")
  def email = column[String]("EMAIL")
  def phone = column[String]("PHONE")
  def mobile = column[String]("MOBILE")
  def work  = column[String]("WORK")
  def role = column[String]("ROLE")
  def lastconnect = column[String]("LASTCONNECT")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, userid , pass , name, position, company, email, phone, mobile, work, role, lastconnect ) <> (User.tupled, User.unapply)
  def noId = (userid , pass , name, position, company, email, phone, mobile, work, role, lastconnect )
}