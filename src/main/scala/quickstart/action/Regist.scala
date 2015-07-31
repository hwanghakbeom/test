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
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

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
		jsRespond("alert(" + jsEscape("아이디가 중복됩니다.") + ")") 		
	}
}

@POST("updatepc")
class UpdatePC extends DefaultLayout {
  def execute() {
    var name   = param("name")
    var add1   = param("add1")
    var add2   = param("add2")
    var add3   = param("add3")
    var owner  = param("owner")
    var phone  = param("phone")
    var mobile   = param("mobile")
    var channel  = param("channel")
    var lastdate   = param("lastdate")
    var ischecked   = param("isfinished")
    var rid = paramo("rid")  match{
            case Some(x:String) => x 
            case _ => ""
          }

    var regdate = TransDate.getCurrentDate()
    val pcs: TableQuery[Pcs] = TableQuery[Pcs]
    val db = forURL()
      db withSession { implicit session =>
        if(rid == ""){
          pcs += Pc(None,name, add1, add2, add3, owner, phone, mobile, channel, lastdate, ischecked,regdate)
        }
        else {
          pcs.filter(_.rid === rid.toInt)
       .map(p => (p.name,p.add1,p.add2,p.add3,p.owner,p.phone,p.mobile,p.channel,p.lastdate,p.isfinished))
       .update((name,add1,add2,add3,owner,phone,mobile,channel,lastdate,ischecked))
        }
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
      var q1 = channel.filter(_.name === name).list
      if(q1.size > 0) { jsRespond("alert(" + jsEscape("Not Found") + ")") }
      else{ channel += Channel(None,name,userid.toString) 
          respondJson("okay")       
      }  
		  }

	}
}

@POST("newgame")
class Newgame extends DefaultLayout {	
  def execute() {
    var gameid   = param("rid")
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
        if(gameid == ""){
          game += Game(None,name,company,companynumber,owner,add1,startdate,enddate,ratio,ratiodetail,add2)
        }
        else{
            game.filter(p => p.rid === gameid.toInt)
         .map(p => (p.name,p.companyname,p.companynumber,p.owner,p.add1,p.startdate,p.enddate,p.ratio,p.ratiodetail,p.add2))
         .update((name,company,companynumber,owner,add1,startdate,enddate,ratio, ratiodetail,add2))
        }
		  	
		  }
		respondJson("okay") 
  }
}

@POST("newuser")
class Newuser extends DefaultLayout { 
  def execute() {
    var userid   = param("userid")
    var username = param("username") 
    var userpass = param("userpass")
    var position = param("position")
    var usercompany   = param("usercompany")
    var useremail = param("useremail") 
    var userphone  = param("userphone")
    var usermobile = param("usermobile").toString 
    var userwork  = param("userwork").toString
    var finishdate = param("finishdate").toString
    var isfinished  = param("isfinished").toString

    val user: TableQuery[Users] = TableQuery[Users]
    val db = forURL()
      db withSession { implicit session =>
        //userid 가 있으면 업데이트
        var q1 = user.filter(q => q.userid === userid).list
        if(q1.size > 0)
        {
          //업데이트
          if(userpass == "[object HTMLInputElement]"){
              user.filter(p => p.userid === userid)
           .map(p => (p.name,p.position,p.company,p.email,p.phone,p.mobile,p.work))
           .update((username,position,usercompany,useremail,userphone,usermobile,userwork))           
          }
          else{
              user.filter(p => p.userid === userid)
           .map(p => (p.name,p.position,p.pass,p.company,p.email,p.phone,p.mobile,p.work))
           .update((username,position,userpass,usercompany,useremail,userphone,usermobile,userwork))           
          } 
        }
        else
        {
          //없으면 insert
          user += User(None,userid,userpass,username,position,usercompany,useremail,userphone,usermobile,userwork,"",finishdate)
        }        
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

// @POST("newuser")
// class Newuser extends DefaultLayout {	
//   def execute() {
//   	var userid = param("userid")
//   	var username = param("username")
//   	var usercompany = param("usercompany")
//   	var useremail = param("useremail")
//   	var userphone = param("userphone")
//   	var usermobile = param("usermobile")
//   	var userwork = param("userwork")
//   	var finishdate = param("finishdate")
//   	var isfinished = param("isfinished")
//   	val db = forURL()
//   	val user: TableQuery[Users] = TableQuery[Users]
//   	db withSession { implicit session =>
//   			user += User(None,userid , "" , username, "", usercompany, useremail, userphone, usermobile, userwork, userwork, finishdate)
//   	}	
//   	  	respondJson("okay")
//   }
//  }

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
          var removedollar = arr2(3).replace("$","")
          println(arr2)
          mapping += Mapping(None,arr2(0),arr2(1),arr2(2),removedollar,TransDate.getCurrentDate())
    } 
        respondJson("okay")
  }
 }
}
@GET("/user/channelcheck")
class ChannelCheck extends DefaultLayout { 
  def execute() {
    var param1 = param("channel-name")
    val db = forURL()
    db withSession { implicit session =>
      val channel: TableQuery[Channels] = TableQuery[Channels]
      var q1 = channel.filter(_.name === param1).list
      if(q1.size > 0) { jsRespond("alert(" + jsEscape("Not Found") + ")") }
      else {respondJson("okay")}
    }
    }
  }

