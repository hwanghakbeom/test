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
        var queryString = "select count(*) as c, sum(ipcount) as ip, installdate from install where game = ? and address = ? and installdate between '"+ date1 +"' and '"+ date2 + "' group by installdate,address"
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
    var userid = session("userId")
    var patternt = "\\d+".r
    var regresult = patternt findAllIn userid.toString
    var llist = regresult.toList
    var rid = llist(0)
    // After login success
      val db = forURL()
      db withSession { implicit session =>
        var queryString = "select name from channel where user= ?"
        var result = Q.query[String,(String)](queryString)
        val period = result(rid).list
        var channellist = scala.collection.mutable.MutableList[Map[Any,Any]]()
        if(period.size > 0 )
        {
          for (t <- period) {
            channellist += Map("list" -> t)
          }
        }
        at("channellist") = channellist
    var channelname = paramo("select-game")  match{
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
    if(channelname == ""  || date1 == "" || date2 == ""){
      println("login")
    }
    else{
      at("query") = channelname +  " , " + date1 + " ~ " + date2
      val db = forURL()
      db withSession { implicit session =>

          //gamelist
          var valuelist = scala.collection.mutable.MutableList[Map[Any,Any]]()
          var returnlist = scala.collection.mutable.MutableList[Map[Any,Any]]()
           var gamequerystring = "select game from install where channel = ? group by game order by game"
           var gamequeryresut = Q.query[String,(String)](gamequerystring)
           val gameperiod = gamequeryresut(channelname).list
           var gamelist = scala.collection.mutable.MutableList[Map[Any,Any]]()
            if(gameperiod.size > 0 )
            {
              for (t <- gameperiod) {
                gamelist += Map("list" -> t)
                valuelist += Map("pc" -> "0")
                valuelist += Map("pc" -> "0")
            }
            at("games") = gamelist
          }  
           var queryString2 = "select installdate, sum(ipcount) as sum, count(*) as c, game from install where channel = ? group by installdate,game order by installdate,game"
           var result2 = Q.query[String,(String,String,String,String)](queryString2)
           val period2 = result2(channelname).list
           var querysublist = Map[String,String]()
           var querylist = scala.collection.mutable.MutableList[Map[String,String]]()

            if(period2.size > 0 )
            {
              var installdate = ""
              for (t <- period2) {
                if(installdate == t._1) {
                  //같은 날짜 다른 게임
                  //update array
                  var index = 0
                  var findindex = 0
                  for (t1 <- gameperiod) {
                    println("T1 " + t1 + "  t._4: " + t._4)
                    if(t1 == t._4){
                      findindex = index
                    }
                    index = index + 1
                  }
                  println("INDEX = " + findindex.toString)
                  var firstindex = findindex * 2
                  var secondindex = firstindex + 1
                  valuelist(firstindex) = Map("pc" -> t._3)
                  valuelist(secondindex) = Map("pc" -> t._2)
                  println(valuelist(firstindex))
                  println(valuelist(secondindex))
                }
                else{
                  //다른 날짜
                  if(installdate != "") { 
                    //처음 array가 아님
                    //querysublist += Map("value" -> gamesublist)
                    querylist += querysublist
                    println("   INSERTED")
                    println("BACKUP")
                    returnlist += Map("value" -> valuelist,"date" -> installdate)
                    //initial
                      valuelist = scala.collection.mutable.MutableList[Map[Any,Any]]()
                      for (t <- gameperiod) {
                          valuelist += Map("pc" -> "0")
                          valuelist += Map("pc" -> "0")
                      }
                  }
                  installdate = t._1
                  querysublist += ("installdate" -> t._1)
                  //game 위치 파악
                  var index = 0
                  var findindex = 0
                  for (t1 <- gameperiod) {
                    println("T1 " + t1 + "  t._4: " + t._4)
                    if(t1 == t._4){
                      findindex = index
                    }
                    index = index + 1
                  }
                  println("INDEX = " + findindex.toString)
                  //update array
                  var firstindex = findindex * 2
                  var secondindex = firstindex + 1
                  valuelist(firstindex) = Map("pc" -> t._3)
                  valuelist(secondindex) = Map("pc" -> t._2)
                  println(valuelist(firstindex))
                  println(valuelist(secondindex))
                }
            
            }
            returnlist += Map("value" -> valuelist,"date" -> installdate)
            println(returnlist)
            at("sumlist") = returnlist
          }
       
             
      }
    }
        //조회
       
    respondView(Map("type" ->"mustache"))
  }
}
}