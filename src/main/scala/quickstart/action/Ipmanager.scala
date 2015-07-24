package quickstart.action

import xitrum.annotation.{POST,GET,DELETE}
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

@GET("ipmanager/:code")
class Ipmanager extends DefaultLayout {	
  def execute() {
    // After login success
    var code = param("code")
    val db = forURL()
    var gamelist = Map[Any,Any]()
     var returngamelist = scala.collection.mutable.MutableList[Map[Any,Any]]()
    db withSession { implicit session =>
    var queryString = "SELECT * FROM game WHERE 1 = ?"
    var query1 = Q.query[String, (String,String,String,String,String,String,String,String,String,String,String)](queryString)
    val peroid2 = query1("1").list
    if(peroid2.size > 0 )
    {
        for (t <- peroid2) {
          gamelist = Map("rid" -> t._1,
            "name" -> t._2)
          returngamelist += gamelist
        }
    }

     var q2 = Q.query[String,(String,String,String,String)]("SELECT * FROM ips where pcsid = ?")
     val peroid = q2(code).list
     var returnlist = scala.collection.mutable.MutableList[Map[Any,Any]]()

      var iplist = Map[Any,Any]()
      var gamesublist = Map[Any,Any]()
      
      for (gt <- peroid) {
        var gamelist = scala.collection.mutable.MutableList[Map[Any,Any]]()
        for (t <- peroid2) {
          var gameq = Q.query[String,(String)]("SELECT installdate FROM  ipgame where ip = ? and game = '" + t._2 + "'")
          val peroid3 = gameq(gt._3).list
          var inputvalue = ""
          if(peroid3.size > 0) { inputvalue = peroid3(0)}
          gamesublist = Map("count" -> inputvalue)
          gamelist += gamesublist
        }
          iplist = Map(
          "ip" -> gt._3,
          "game" -> gamelist
          )
        returnlist += iplist
      }



    //installation date


            at("value") = returnlist 
            at("games") = returngamelist  	

    }


	respondView(Map("type" ->"mustache"))
  }
}
@POST("ipmanager/")
class PostIpmanager extends DefaultLayout {	
  def execute() {
  	var pcid = param("pcs")
  	var startip = param("startip")
  	var endip = param("endip")
  	var work = param("work")
  	var date = param("date")
	var patternt = "\\d+".r
	var arr = startip.split(".".toArray)
	var iptext = arr(0) + "." + arr(1) + "." + arr(2)
	println(iptext)
    var regresult = patternt findAllIn pcid
    var llist = regresult.toList
    println(llist.toString)
    if(work =="IP작업"){
    	val db = forURL()
  		val ips: TableQuery[Ips] = TableQuery[Ips]
  		db withSession { implicit session =>

        //ipcheck
        if(endip != ""){
            var ipcheckstring = ""
            for(index <- arr(3).toInt to endip.toInt){
              var ip = iptext + "." + index.toString
              var queryString = "SELECT ip FROM ips WHERE ip = ?"
              var q1 = Q.query[String, (String)](queryString)
              val peroid = q1(ip).list
              if(peroid.size > 0 )
              {
                jsRespond("alert(" + jsEscape("아이디가 중복됩니다."  ) + ")") 
              }
            }
            for(index <- arr(3).toInt to endip.toInt){
              var ip = iptext + "." + index.toString
              ips += Ip(None,llist(0), startip , date)
            }           
          }
        else
        {
              var queryString = "SELECT ip FROM ips WHERE ip = ?"
              var q1 = Q.query[String, (String)](queryString)
              val peroid = q1(startip).list
              if(peroid.size > 0 )
              {
                jsRespond("alert(" + jsEscape("아이디가 중복됩니다."  ) + ")")     
              }
              ips += Ip(None,llist(0), startip , date)
        }
  		}
    }
    else
    {
      val db = forURL()
      val ipgame: TableQuery[Ipgames] = TableQuery[Ipgames]
      db withSession { implicit session =>
        if(endip != ""){
          for(index <- arr(3).toInt to endip.toInt){
          var ip = iptext + "." + index.toString
          ipgame += Ipgame(None,llist(0), ip , work,date)
          }
        }
        else{
          ipgame += Ipgame(None,llist(0), startip , work,date)
        }

      }
    }
	respondJson("okay")
  }
}

@POST("ipmanagerdelete/")
class DeleteIpmanager extends DefaultLayout {	
  def execute() {
  	var pcid = param("pcs")
  	var startip = param("startip")
  	var endip = param("endip")
  	var work = param("work")
  	var date = param("date")
	var patternt = "\\d+".r
	var arr = startip.split(".".toArray)
	var iptext = arr(0) + "." + arr(1) + "." + arr(2)
	println(iptext)
    var regresult = patternt findAllIn pcid
    var llist = regresult.toList
    println(llist.toString)
    if(work =="IP작업"){
    	val db = forURL()
  		val ips: TableQuery[Ips] = TableQuery[Ips]
  		db withSession { implicit session =>
        if(endip != ""){
          for(index <- arr(3).toInt to endip.toInt){
            var ip = iptext + "." + index.toString
            def deletegame(ip: String) = sqlu"delete from ips where ip = $ip".first
              val rows= deletegame(ip)
          }          
        }
        else{
          def deletegame(ip: String) = sqlu"delete from ips where ip = $ip".first
          val rows= deletegame(startip)
        }

  		}
    }
    else
    {
      val db = forURL()
      val ips: TableQuery[Ips] = TableQuery[Ips]
      db withSession { implicit session =>
        if(endip != ""){
          for(index <- arr(3).toInt to endip.toInt){
            var ip = iptext + "." + index.toString
            def deletegame(ip: String, game:String) = sqlu"delete from ipgame where ip = $ip and game = $game".first
            val rows= deletegame(ip,work)
          }
        }
        else{
           def deletegame(ip: String, game:String) = sqlu"delete from ipgame where ip = $ip and game = $game".first
            val rows= deletegame(startip,work)
        }
      }

    }
	respondJson("okay")
  }
}