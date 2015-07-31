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


object SVar {
  object username extends SessionVar[String]
}

@GET("login")
class Login extends DefaultLayout {	
  def execute() {
    // After login success
    var user = session.getOrElse("role", null)
    if(user != null){
	    if(session("role") == "cha"){
	    	redirectTo("/installbyc")
	    }
	    else if(session("role") == "adv"){
	    	redirectTo("/installbyg")
	    }
	    else if(session("role") == "admin"){
	    	redirectTo("/installbyg")
	    }

    }
	respondView(Map("type" ->"mustache"))
  }
}


@POST("createuser")
class Createuser extends DefaultLayout {
	def execute() {
    var userid = param("userid")
	var		pass = param("pass")
	var		name = param("email")
	var		email = param("phone")
	var		phone = param("phone")
	val users: TableQuery[Users] = TableQuery[Users]
	val db = forURL()
	db withSession { implicit session =>
			users += User(None,userid, pass, name, email, phone,"","","","","","")
		}
	respondJson("okay")
	}

}
@GET("logout")
class Logout extends DefaultLayout {
  def execute() {
    // After login success
    session.clear()
	redirectTo("/")
  }
}
@GET("user/idcheck")
class Idcheck extends DefaultLayout {
  def execute() {
    // After login success
    var param1 = param("userid")
		val users: TableQuery[Users] = TableQuery[Users]
		val db = forURL()
		  db withSession { implicit session =>
		    // val filterQuery: Query[Users, ( String, String, String, String, String, String, String, String, Int), Seq] =
		    //   users.filter(_.email === email)
		 	val q1 = users.filter(_.userid === param1).list
		 	if(q1.size > 0) {
		 		jsRespond("alert(" + jsEscape("USER ID ALREADY EXIST") + ")")
		 	}
		 	else{
		 		respondJson("okay")
		 	}
		 }
  }
}


@POST("login")
class checkLoginID extends DefaultLayout {
	def execute() {
		var userid	 = param("userid")
		var pass	 = param("password")
		var role = ""
		val users: TableQuery[Users] = TableQuery[Users]
		session.clear()
		var isokay = false;
		val db = forURL()
		  db withSession { implicit session =>
		  	var q2 = users.filter(p => p.userid === userid && p.pass === pass).list
		  	if(q2.size > 0) {	
		  		isokay = true
		  		var test = q2(0).productIterator.toList.zip(List("rid", "userid" , "pass" , "name", "position", "company", "email", "phone", "mobile", "work", "role", "lastconnect" ))
				  	role = test(10)._1.toString
				  	userid = test(0)._1.toString
				  	
		  	}
		  }
		  if(isokay){
		  	session("userId") = userid
		  	session("role") = role
		  	if(role == "adv") {
		  		redirectTo("installbyg")
		  	}
		  	else
		  	{
				redirectTo("pc")
		  	}
		  	}
		    else{
		  	redirectTo("/login")
		  }	
		  }
}

