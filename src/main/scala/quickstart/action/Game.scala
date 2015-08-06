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

@GET("gamemanager")
class Gamemanager extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    var userid = session.getOrElse("userId", "")
	val db = forURL()
    var patternt = "\\d+".r
    var regresult = patternt findAllIn userid.toString
    var llist = regresult.toList
    var rid = llist(0)
	var sublist = Map[Any,Any]()
	var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
	db withSession { implicit session =>
	var queryString = ""
	if(rid == "311")
	{
		queryString = "select * from mapping where 1 = ?"
		var q1 = Q.query[String,(String,String,String,String,String,String)](queryString)
		val peroid = q1("1").list
		if(peroid.size > 0) {
			for (t <- peroid) {
				sublist =Map("rid" -> t._1,
		            "channel" -> t._2,
		            "game" ->t._3,
		            "type" ->t._4,
		            "directory" -> t._5,
		            "regdate" -> t._6)
		        returnList += sublist 	
			}
		}
		at("value") = returnList
		println(returnList)
		println("admin")
	}
	else
	{
		queryString = "select * from mapping where channel = (select name from channel where rid = (select work from users where rid = ?))"
		var q1 = Q.query[String,(String,String,String,String,String,String)](queryString)
		val peroid = q1(rid).list
		if(peroid.size > 0) {
			for (t <- peroid) {
				sublist =Map("state" -> "false",
          "rid" -> t._1,
		            "channel" -> t._2,
		            "game" ->t._3,
		            "type" ->t._4,
		            "directory" -> t._5,
		            "regdate" -> t._6)
		        returnList += sublist 	
			}
		}
		at("value") = returnList
		println(returnList)
		println("user")
	}
	}


    var gamereturnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var channelList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    sublist = Map[Any,Any]()
    var gamelist = Map[Any,Any]()

    val channel: TableQuery[Channels] = TableQuery[Channels]
    if(session("role") == "cha"){
      db withSession { implicit session =>
        var excelquery = Q.query[String,(String,String)]("select rid, name from channel where rid = (select work from users where rid = ?)")
        val peroid = excelquery(rid).list
        for (t <- peroid) {
          var channelsublist = Map[Any,Any]()
          channelsublist = Map(
            "rid" -> t._1,
            "name" -> t._2
            ) 
           channelList += channelsublist         
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
          var test = q1(index).productIterator.toList.zip(List("rid", "name", "user"))
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
        gamereturnList += gamelist
      }
    }
    at("game") = gamereturnList
    at("channel") = channelList
	}
  	respondView(Map("type" ->"mustache"))
  }
}

@POST("gamedelete/:rid")
class Gamedelete extends DefaultLayout { 
  def execute() {
    val rid = param("rid")
    val db = forURL()
    db withSession { implicit session =>
            def deleteip(ip: String) = sqlu"delete from mapping where rid = $rid".first
              val rows= deleteip(rid)     
    }
    respondJson("okay")

  }
}