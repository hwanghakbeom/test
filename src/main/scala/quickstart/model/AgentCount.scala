package quickstart.action

import xitrum.validator.Required
import scala.slick.driver.H2Driver.simple._

case class Agent(rid: Option[Int],name: String, cnt: String, installdate:String)

class Agents(tag: Tag) extends Table[Agent](tag,"AGENTCOUNT") {
  def rid = column[Int]("RID",O.AutoInc) // This is the primary key column
  def name = column[String]("NAME")
  def cnt = column[String]("CNT",O.PrimaryKey)
  def installdate = column[String]("INSTALLDATE")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (rid.?, name,cnt , installdate ) <> (Agent.tupled, Agent.unapply)
  def noId = (name,cnt , installdate)
}