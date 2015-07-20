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

// @GET("user/mailcheck")
// class Mailcheck extends DefaultLayout {
//   def execute() {
//     // After login success
//     var param1 = param("email")
// 		val users: TableQuery[Users] = TableQuery[Users]
// 		val db = forURL()
// 		  db withSession { implicit session =>
// 		    // val filterQuery: Query[Users, ( String, String, String, String, String, String, String, String, Int), Seq] =
// 		    //   users.filter(_.email === email)
// 		 	val q1 = users.filter(_.email === param1).list
// 		 	if(q1.size > 0) {
// 		 		jsRespond("alert(" + jsEscape("Not Found") + ")")
// 		 	}
// 		 	else{
// 		 		respondJson("okay")
// 		 	}
// 		 }
//   }
// }


@POST("login")
class checkLoginID extends DefaultLayout {
	def execute() {
		var userid	 = param("userid")
		var pass	 = param("password")
		val users: TableQuery[Users] = TableQuery[Users]
		session.clear()
		var isokay = false;
		val db = forURL()
		  db withSession { implicit session =>
		  	var q2 = users.filter(p => p.userid === userid && p.pass === pass).list
		  	if(q2.size > 0) {	isokay = true		  	}
		  }
		  if(isokay) {
		  	session("userId") = userid
		  	redirectTo("pc")
		  }
		  else{
		  	redirectTo("/login")
		  }		  		
	}
}

