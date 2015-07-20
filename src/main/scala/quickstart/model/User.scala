package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class User(rid: Option[Int],userid: String, pass: String, name: String, email: String, phone: String)

class Users(tag: Tag) extends Table[User](tag,"users") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def userid = column[String]("USERID")
  def pass = column[String]("PASS")
  def name = column[String]("NAME")
  def email  = column[String]("EMAIL")
  def phone = column[String]("PHONE")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, userid , pass , name, email, phone ) <> (User.tupled, User.unapply)
  def noId = (userid , pass , name ,email, phone )
}