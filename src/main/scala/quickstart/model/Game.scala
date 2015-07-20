package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Game(rid: Option[Int],ip: String, name: String)

class Games(tag: Tag) extends Table[Game](tag,"game") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def ip = column[String]("IP")
  def name = column[String]("NAME")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, ip , name ) <> (Game.tupled, Game.unapply)
  def noId = (ip , name)
}