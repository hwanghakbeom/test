package quickstart.action

import xitrum.annotation.{POST,GET}
import xitrum.SessionVar
import java.security.SecureRandom
import java.math.BigInteger
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta.MTable
import xitrum.util.SeriDeseri
import scalaj.http.Http
import scalaj.http.HttpOptions
import org.jboss.netty.handler.codec.http.HttpHeaders
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jsoup.Jsoup
import quickstart._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

@GET("pc")
class Pcstatus extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var channelList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    var gamelist = Map[Any,Any]()

    var userid = session("userId").toString 

    var patternt = "\\d+".r
    var regresult = patternt findAllIn userid
    var llist = regresult.toList
    var rid = llist(0)

    val channel: TableQuery[Channels] = TableQuery[Channels]
    val db = forURL()
    if(session("role") == "cha"){
      db withSession { implicit session =>
          var queryString = "select rid,name from channel where rid = (select work from users where rid = ?)"
          var result = Q.query[String,(String,String)](queryString)
          val period = result(rid).list
          var channelsublist = Map[Any,Any]()
          if(period.size > 0 )
          {
            for (t <- period) {
              channelsublist = Map("rid" -> t._1, "name" -> t._2)
              channelList += channelsublist
            }
          }   
      }
    }
    else
    {
      db withSession { implicit session =>
        var q1 = channel.filter(_.rid > 0).list
        val querysize = q1.size - 1
        channelList += Map("rid"->"0", "name"->"채널전체")
        for(   index <-0 to querysize ){
          var test = q1(index).productIterator.toList.zip(List("rid", "name", "company","regnumber","owner","address","regdate","enddate","user"))
          var channelsublist = Map[Any,Any]()
          channelsublist = Map(
            test(0)._2 -> test(0)._1, 
            test(1)._2 -> test(1)._1
            )
          channelList += channelsublist
        }        
      }
    }
    db withSession { implicit session =>
    var queryString = "SELECT * FROM game WHERE 1 = ?"
    var query1 = Q.query[String, (String,String,String,String,String,String,String,String,String,String,String)](queryString)
    val peroid = query1("1").list
    if(peroid.size > 0 )
    {
      for (t <- peroid) {
        gamelist = Map("rid" -> t._1,
          "name" -> t._2)
        returnList += gamelist
      }
    }
    at("game") = returnList
    at("channel") = channelList
    respondView(Map("type" ->"mustache"))
  }
}
}

@GET("calcrange")
class Calcrange extends DefaultLayout {  
  def execute() {
    val db = forURL()
db withSession { implicit session =>
      var queryString = "select rid from pcs where 1= ? "
      var countQuery = Q.query[String,(String)](queryString)
      var dateResult = countQuery("1").list
      for (t <- dateResult) {
        var ipqu = "select ip from ips where pcsid = ?"
        var cq = Q.query[String,(String)](ipqu)
        var dateResul = cq(t).list
        var min = 999
        var max = 0
        var updatedip = ""
        for (t1 <- dateResul) {
              var arr = t1.split(".".toArray)
              var intvalue = arr(3).toInt
              if(min > intvalue) { min = intvalue}
              if(max < intvalue) { max = intvalue}
              updatedip = arr(0) + "." + arr(1) + "." + arr(2) + "." + min.toString + "~" + max.toString
              // var iptext = 
        }
        println(updatedip + " " + t)

      }     
    }
        respondJson("ok")
    }

  }


@GET("calcagent")
class Calcagent extends DefaultLayout {  
  def execute() {
    val db = forURL()
    val agents: TableQuery[Agents] = TableQuery[Agents]
      db withSession { implicit session =>
      var channelListString = "SELECT AA.name, IFNULL(BB.cnt, 0) as cnt from"
      channelListString += " (SELECT * from channel) AA left join"
      channelListString += " (SELECT A.channel, count(*) as cnt from"
      channelListString += " (select * from pcs where rid) A ,"
      channelListString += " (select pcsid from ips where ip in (select ip from ipnumber where installdate = ?) group by pcsid) B"
      channelListString += " where A.rid = B.pcsid"
      channelListString += " group by channel) BB"
      channelListString += " on AA.name = BB.channel"
      var channelListQuery = Q.query[String,(String,String)](channelListString)
      for(index <-1 to 30 ){
        var agentString = "select installdate from AGENTCOUNT where installdate = ? group by installdate"
        var agentListQuery = Q.query[String,(String)](agentString)
        var agentListResult = agentListQuery(TransDate.getBeforeDay(index,"current")).list
        if(agentListResult.size == 0)
        {
          var channelListResult = channelListQuery(TransDate.getBeforeDay(index,"current")).list
          for (t <- channelListResult){
            agents += Agent(None,t._1, t._2 , TransDate.getBeforeDay(index,"current"))
          }          
        }

      }     
              respondJson("ok")
    }

    }
  }

@GET("calcagenttoday")
class Calcagenttoday extends DefaultLayout {  
  def execute() {
    val db = forURL()
    val agents: TableQuery[Agents] = TableQuery[Agents]
      db withSession { implicit session =>
      var channelListString = "SELECT AA.name, IFNULL(BB.cnt, 0) as cnt from"
      channelListString += " (SELECT * from channel) AA left join"
      channelListString += " (SELECT A.channel, count(*) as cnt from"
      channelListString += " (select * from pcs where rid) A ,"
      channelListString += " (select pcsid from ips where ip in (select ip from ipnumber where installdate = ?) group by pcsid) B"
      channelListString += " where A.rid = B.pcsid"
      channelListString += " group by channel) BB"
      channelListString += " on AA.name = BB.channel"
      var channelListQuery = Q.query[String,(String,String)](channelListString)
      var channelListResult = channelListQuery(TransDate.getBeforeDay(0,"current")).list
            def cleandummy(ip: String) = sqlu"delete from AGENTCOUNT where installdate = ''".first
              var rows= cleandummy(TransDate.getBeforeDay(0,"current")) 
              println(s"Deleted $rows rows")

        for (t <- channelListResult){
            agents += Agent(None,t._1, t._2 , TransDate.getBeforeDay(0,"current"))
        }          

              respondJson("ok")
    }

    }
  }