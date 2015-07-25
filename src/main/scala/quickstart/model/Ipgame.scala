package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Ipgame(rid: Option[Int],pcid: String, ip: String, game:String, installdate:String, confirmed:String)

class Ipgames(tag: Tag) extends Table[Ipgame](tag,"ipgame") {
  def rid = column[Int]("RID", O.PrimaryKey,O.AutoInc) // This is the primary key column
  def pcid = column[String]("PCID")
  def ip = column[String]("IP")
  def game = column[String]("GAME")
  def installdate = column[String]("INSTALLDATE")
  def confirmed = column[String]("CONFIRMED")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, pcid,ip ,game, installdate,confirmed ) <> (Ipgame.tupled, Ipgame.unapply)
  def noId = (pcid,ip ,game, installdate,confirmed)
}