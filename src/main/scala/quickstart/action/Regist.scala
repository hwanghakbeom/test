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
  	var ips = param("ips")
  	var arr = ips.split(";")
  	val db = forURL()
  	val ips: TableQuery[Ips] = TableQuery[Ips]
  	var fruits = ArrayBuffer[String]()
  	db withSession { implicit session =>
  		for (t <- arr) {
  			var q1 = ips.filter(_.ip === t).list
  			if(q1.size > 0) { fruits += t }
  		}	
  	}	
  }
  	respondJson("okay")
  	}
 }

 @POST("newip")
class Newip extends DefaultLayout {	
  def execute() {
  	var ips = param("ips")
  	var arr = ips.split(";")
  	val db = forURL()
  	val ips: TableQuery[Ips] = TableQuery[Ips]
  	var fruits = ArrayBuffer[String]()
  	db withSession { implicit session =>
  		for (t <- arr) {
  			var q1 = ips.filter(_.ip === t).list
  			if(q1.size > 0) { fruits += t }
  		}	
  	}	
  }
  	respondJson("okay")
  	}
 }