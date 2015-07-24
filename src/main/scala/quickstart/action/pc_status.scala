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

    var userid = session("userId") 
    val channel: TableQuery[Channels] = TableQuery[Channels]
    val db = forURL()
    if(session("role") == "cha"){
      db withSession { implicit session =>
        var q1 = channel.filter(_.user === userid.toString).list
        val querysize = q1.size - 1
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
    else
    {
      db withSession { implicit session =>
        var q1 = channel.filter(_.rid > 0).list
        val querysize = q1.size - 1
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
        returnList += gamelist
      }
    }
    at("game") = returnList
    at("channel") = channelList
    respondView(Map("type" ->"mustache"))
  }
}
}