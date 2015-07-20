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
