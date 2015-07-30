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
		queryString = "select * from mapping where channel = (select name from channel where user = ?)"
		var q1 = Q.query[String,(String,String,String,String,String,String)](queryString)
		val peroid = q1(rid).list
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
		println("user")
	}
	}
  	respondView(Map("type" ->"mustache"))
  }
}