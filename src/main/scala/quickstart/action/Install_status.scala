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
    var date2 = paramo("date2")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    val games: TableQuery[Games] = TableQuery[Games]
    var gameselectlist = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sumlist = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var gameselectsublist = Map[Any,Any]()
    var subsumlist = Map[Any,Any]()
    if(session("userId") == "" || session("role") == "cha"){
      redirectTo("/installbyc")
    }
    var userid = session("userId").toString

    var patternt = "\\d+".r
    var regresult = patternt findAllIn userid
    var llist = regresult.toList
    var rid = llist(0)

    if(session("role") == "adv"){
      //광고주일때
      val db = forURL()
      db withSession { implicit session =>
        var q2 = games.filter(p => p.rid === rid.toInt).list
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
    println("========================================================")
    println(gamename)
    println(region)
    println(date1)
    println(date2)
    println("========================================================")
    if(gamename == ""  || region == "" || date1 == "" || date2 == ""){
      println("login")
    }
    else
    {
      val db = forURL()
      db withSession { implicit session =>
        var queryString = "select count(*) as c, sum(ipcount) as ip, installdate from install where game = ? and address = ? group by installdate,address"
        var result = Q.query[(String,String),(String,String,String)](queryString)
        val period = result(gamename,region).list
        var ipcount = scala.collection.mutable.MutableList[Map[Any,Any]]()
        var sublist = Map[Any,Any]()
        var counttotal = 0
        var iptotal = 0
        if(period.size > 0 )
        {
          for (t <- period) {
            sublist =Map("date" -> t._3,
              "count" -> t._1,
              "sum" -> t._2)
            counttotal = counttotal + t._1.toInt
            iptotal = iptotal + t._2.toInt
           ipcount += sublist
          }
        }
        at("query") = gamename + ", " + region + " , " + date1 + " ~ " + date2
        at("region") = region
        at("sumlist") = ipcount

        //로컬 PC방 카운트
        var pclocalcountquery = "select rid from pcs  where left(add2,2) = ?"
        var pclocalcountresult = Q.query[String,(String)](pclocalcountquery)
        var pclocalcount = pclocalcountresult(region).list

        at("pclocalcount") = pclocalcount.size

        //로컬 IP 카운트
        var iplocalcountquery = "select A.rid from ips A, pcs B where A.pcsid = B.rid and left(add2,2) = ?"
        var iplocalcountresult = Q.query[String,(String)](iplocalcountquery)
        var iplocalcount = iplocalcountresult(region).list

        at("iplocalcount") = iplocalcount.size

        //PC방 카운트
        var pctotalcountquery = "select rid from pcs where 1 = ?"
        var pctotalcountresult = Q.query[String,(String)](pctotalcountquery)
        var pctotalcount = pctotalcountresult("1").list

        at("pctotalcount") = pctotalcount.size

        //IP 카운트
        var iptotalcountquery = "select rid from ips where 1 = ?"
        var iptotalcountresult = Q.query[String,(String)](iptotalcountquery)
        var iptotalcount = iptotalcountresult("1").list

        at("iptotalcount") = iptotalcount.size

        if(region == "전국"){
           var infosub = Map("pclocalcount" -> pclocalcount.size,
            "iplocalcount" -> iplocalcount.size,
            "pctotalcount" -> pctotalcount.size,
            "iptotalcount" -> iptotalcount.size)   
        }
        else {
           var infosub = Map("pclocalcount" -> pclocalcount.size,
            "iplocalcount" -> iplocalcount.size)          
        }
        at("counttotal") = counttotal
        at("iptotal") = iptotal




    }
  }
    respondView(Map("type" ->"mustache"))
}
}
@GET("installbyc")
class Installbyc extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    if(session("userId") == "" || session("role") == "adv"){
      redirectTo("/installbyg")
    }
    // After login success
      val db = forURL()
      db withSession { implicit session =>
        var queryString = "select name from channel where 1= ?"
        var result = Q.query[String,(String)](queryString)
        val period = result("1").list
        var channellist = scala.collection.mutable.MutableList[Map[Any,Any]]()
        if(period.size > 0 )
        {
          for (t <- period) {
            channellist += Map("list" -> t)
          }
        }
        at("channellist") = channellist
    var gamename = paramo("select-game")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    var date1 = paramo("date1")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    var date2 = paramo("date2")  match{
            case Some(x:String) => x 
            case _ => ""
          }
    if(gamename == ""  || date1 == "" || date2 == ""){
      println("login")
    }
    else{
      val db = forURL()
      db withSession { implicit session =>
           var queryString2 = "select installdate, sum(ipcount) as sum, count(*) as c, game from install where channel = ? group by game order by installdate"
      }
    }
        //조회
       
    respondView(Map("type" ->"mustache"))
  }
}
}