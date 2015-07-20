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
import scala.collection.mutable.ArrayBuffer
import quickstart._


@POST("newpc")
class NewPC extends DefaultLayout {
	def execute() {
		var name	 = param("name")
		var add1	 = param("add1")
		var add2	 = param("add2")
		var add3	 = param("add3")
		var owner	 = param("owner")
		var phone	 = param("phone")
		var mobile	 = param("mobile")
		var channel	 = param("channel")
		var lastdate	 = param("lastdate")
		var isfinished	 = param("isfinished")
		var ischecked = "false"
		if(isfinished == "checked") { ischecked = "true"}
		var regdate = TransDate.getCurrentDate()
		val pcs: TableQuery[Pcs] = TableQuery[Pcs]
		val db = forURL()
		  db withSession { implicit session =>
		  	pcs += Pc(None,name, add1, add2, add3, owner, phone, mobile, channel, lastdate, ischecked,regdate)
		  }
		respondJson("okay")  		
	}
}

@POST("newchannel")
class NewChannel extends DefaultLayout {
	def execute() {
		var name	 = param("name")
		 var userid = session("userId") 
		val channel: TableQuery[Channels] = TableQuery[Channels]
		val db = forURL()
		  db withSession { implicit session =>
		  	channel += Channel(None,name,userid.toString)
		  }
		respondJson("okay")  		
	}
}

@POST("newgame")
class Newgame extends DefaultLayout {	
  def execute() {
		var name	 = param("name")
		var add1 = param("add1") 
		var add2	 = param("add2")
		var owner = param("owner") 
		var company	 = param("company")
		var companynumber = param("companynumber").toString 
		var startdate	 = param("startdate").toString
		var enddate = param("enddate").toString
		var ratio	 = param("ratio").toString
		var ratiodetail = param("ratiodetail").toString 

		val game: TableQuery[Games] = TableQuery[Games]
		val db = forURL()
		  db withSession { implicit session =>
		  	game += Game(None,name,company,companynumber,owner,add1,startdate,enddate,ratio,ratiodetail,add2)
		  }
		respondJson("okay") 
  }
}

@GET("ipcheck/:ips")
class Ipcheck extends DefaultLayout {	
  def execute() {
  	var ipsparam = param("ips")
  	var arr = ipsparam.split(";")
  	val db = forURL()
  	val ips: TableQuery[Ips] = TableQuery[Ips]
  	var fruits = ArrayBuffer[String]()
  	db withSession { implicit session =>
  		for (t <- arr) {
  			var q1 = ips.filter(_.ip === t).list
  			if(q1.size > 0) { fruits += t }
  		}	
  	}	
  	  	respondJson("okay")
  }
 }


@POST("newip")
class Newip extends DefaultLayout {	
  def execute() {
  	var ipsparam = param("ips")
  	var regdate = param("regdate")
  	var pcsid = param("pcs")
  	var arr = ipsparam.split(";")
  	val db = forURL()
  	val ips: TableQuery[Ips] = TableQuery[Ips]
  	var fruits = ArrayBuffer[String]()
  	db withSession { implicit session =>
  		for (t <- arr) {
  			ips += Ip(None,pcsid, t, regdate)
  		}	
  	}	
  	  	respondJson("okay")
  }

  	}

@POST("newuser")
class Newuser extends DefaultLayout {	
  def execute() {
  	var userid = param("userid")
  	var username = param("username")
  	var usercompany = param("usercompany")
  	var useremail = param("useremail")
  	var userphone = param("userphone")
  	var usermobile = param("usermobile")
  	var userwork = param("userwork")
  	var finishdate = param("finishdate")
  	var isfinished = param("isfinished")
  	val db = forURL()
  	val user: TableQuery[Users] = TableQuery[Users]
  	db withSession { implicit session =>
  			user += User(None,userid , "" , username, "", usercompany, useremail, userphone, usermobile, userwork, userwork, finishdate)
  	}	
  	  	respondJson("okay")
  }
 }

@POST("reg_direcotry")
class Regdirectory extends DefaultLayout { 
  def execute() {
    var userid = param("directory")
    var arr = userid.split("$")
    println(userid)
    val db = forURL()
    val mapping: TableQuery[Mappings] = TableQuery[Mappings]
    db withSession { implicit session =>
      for (t <- arr) {
          var arr2 = t.split(";")
          println(arr2)
          mapping += Mapping(None,arr2(0),arr2(1),arr2(2),arr2(3),TransDate.getCurrentDate())
    } 
        respondJson("okay")
  }
 }
}


