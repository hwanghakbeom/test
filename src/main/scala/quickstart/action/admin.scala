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

@GET("admin")
class Admin extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    // After login success
	respondView(Map("type" ->"mustache"))
  }
}

@GET("gamedetails/:code")
class Gamedetails extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
  	var code = param("code")
    var sublist = Map[Any,Any]()
    val db = forURL()
      db withSession { implicit session =>
      	var queryString = "SELECT * FROM game where name = ?"
        var q1 = Q.query[String, (String, String,String, String,String, String,String, String,String, String,String)](queryString)
        val peroid = q1(code).list
        for (t <- peroid) {
             sublist = Map("rid" -> t._1,
               "name" -> t._2,
               "companyname" -> t._3,
               "companynumber" -> t._4,
               "owner" -> t._5,
               "add1" -> t._6,
               "startdate" -> t._7,
               "enddate" -> t._8,
               "ratio" -> t._9,
               "ratiodetail" -> t._10,
               "add2" -> t._11
                )
        }
	respondJson(sublist)
  }
}
}
@GET("userdetails/:code")
class Userdetails extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
  	var code = param("code")
    var sublist = Map[Any,Any]()
    val db = forURL()
      db withSession { implicit session =>
      	var queryString = "SELECT * FROM users where userid = ?"
        var q1 = Q.query[String, (String, String,String, String,String, String,String, String,String, String,String,String)](queryString)
        val peroid = q1(code).list
        for (t <- peroid) {
             sublist = Map("userid" -> t._2,
               "username" -> t._4,
               "position" -> t._5,
               "usercompany" -> t._6,
               "useremail" -> t._7,
               "userphone" -> t._8,
               "usermobile" -> t._9,
               "userwork" -> t._10,
               "lastconnect" -> t._11
                )
        }
	respondJson(sublist)
  }
}
}
