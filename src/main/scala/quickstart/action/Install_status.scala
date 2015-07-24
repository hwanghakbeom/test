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

@GET("installbyg")
class Installbyg extends DefaultLayout {	
  def execute() {
    // After login success
    var gamename = paramo("select-game")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    var region = paramo("select-region")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    var date1 = paramo("date1")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    var date2 = paramo("date1")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    val games: TableQuery[Games] = TableQuery[Games]
    var gameselectlist = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sumlist = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var gameselectsublist = Map[Any,Any]()
    var subsumlist = Map[Any,Any]()
    if(session("userId") == "" || session("role") == "channel"){
      redirectTo("/installbyc")
    }
    var userid = session("userId")
    if(session("role") == "adv"){
      //광고주일때

      val db = forURL()
      db withSession { implicit session =>
        var q2 = games.filter(p => p.rid === userid.toString.toInt).list
        if(q2.size > 0) { 
          var test = q2(0).productIterator.toList.zip(List("rid", "name"))
          gameselectsublist = Map("name" -> test(1)._1.toString )
          gameselectlist += gameselectsublist
        }
      }

    }
    else{
      //admin 일때

      val db = forURL()
      db withSession { implicit session =>
      var gamequery = Q.query[String,(String)]("SELECT name FROM game where 1 = ?")
      val peroid = gamequery("1").list
      for (t <- peroid) {
        gameselectsublist = Map("name" -> t)
        gameselectlist += gameselectsublist
      }
    }
    }
    at("gamelist") = gameselectlist

    //조회값
      val db = forURL()
      var regionstring = "("
      db withSession { implicit session =>
        //PC방 지역별
        if(region != "" || region != "전국"){
          
          println(region)
          var regionquery = Q.query[String,(String)]("select rid as sub from pcs where left(add2,2) = ?")
          val rperiod = regionquery(region).list
          for (t <- rperiod) {
              println(t)
              regionstring += t
              regionstring += " , "
          }
           regionstring += ")"
        println(regionstring)
        }


      var querystring = "select pcid, count(*),installdate from ipgame where game = ? and installdate between '" + date1 + "' and '" + date2 + "'"
      if(region != "" || region != "전국"){
          regionstring = regionstring.substring(0,regionstring.length - 4)
          regionstring += ")"
          querystring += " and pcid in " + regionstring + ""
      }
      querystring += ""
      querystring += " group by pcid, installdate  order by installdate, pcid"



      var sumquery = Q.query[String,(String,String,String)](querystring )
      val peroid = sumquery(gamename.toString).list
      for (t <- peroid) {
        subsumlist = Map("date" -> t._3,
          "pcid" -> t._1,
          "count" ->t._2)
        sumlist += subsumlist
      }   
      }
      at("sumlist") = sumlist
    respondView(Map("type" ->"mustache"))
  }
}
@GET("installbyc")
class Installbyc extends DefaultLayout {	
  def execute() {
    if(session("userId") == "" || session("role") == "adv"){
      redirectTo("/installbyg")
    }
    // After login success
    respondView(Map("type" ->"mustache"))
  }
}
@GET("installbycparam")
class Installbycparam extends DefaultLayout {	
  def execute() {
    // After login success
    forwardTo[Installbyc]()
  }
}