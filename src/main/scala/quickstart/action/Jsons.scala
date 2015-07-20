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

@GET("getpcstatus")
class Getpcstatus extends DefaultLayout {	
  def execute() {

    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
     sublist = Map("code"->"12345",
    	"name"->"짱PC방",
    	"region"->"서울",
    	"address"->"서울시 강남구 대치동",
    	"phone"->"02-555-1234",
    	"mobile"->"010-234-5690",
    	"user"->"김대표",
    	"ip"->"122",
    	"status"->"정상",
    	"channel"->"채널A",
    	"regdate"->"2015-06-15",
    	"games"->"게임A,게임B,게임C,게임D")
    returnList += sublist
	respondJson(Map("data" -> returnList))
  }
}

@GET("getpcstatus2/:channel/:region/:check1/:check2/:check3/:check4/:check5/:startdate/:enddate/:condition/:detail/")
class Getpcstatus2 extends DefaultLayout {	
  def execute() {
    var channel = param("channel")
    var region = param("region")
    var check1 = param("check1")
    var check2 = param("check2")
    var check3 = param("check3")
    var check4 = param("check4")
    var check5 = param("check5")
    var startdate = param("startdate")
    var enddate = param("enddate")
    var condition = param("condition")
    var detail = param("detail")

    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    var queryString = ""
    queryString += "SELECT RID,NAME,ADD1,ADD2,ADD3,OWNER,PHONE,MOBILE,CHANNEL,LASTDATE,ISFINISHED, REGDATE FROM PCS WHERE 1 = ?"
    queryString += " AND regdate between '" + startdate + "' and '" + enddate + "'"
    if(channel != "채널선택"){ queryString += " AND CHANNEL = '" + channel +"'"}
    if(region != "지역선택") {queryString += " AND substring(ADD2,1,3) = '" + region +"'"}
    if(condition == "PA" && detail != "") { queryString += " AND NAME = '" + detail +"'" }
    if(condition == "CT" && detail != "") { queryString += " AND OWNER = '" + detail +"'" }
    if(condition == "NY" && detail != "") { queryString += " AND RID = ( SELECT PCSID FROM ips WHERE IP =     '" + detail + "')" }
    
    val db = forURL()
      db withSession { implicit session =>
        var q1 = Q.query[String, (String, String,String, String,String, String,String, String,String, String,String,String)](queryString)
        val peroid = q1("1").list
        for (t <- peroid) {
            var addr = t._4.toString + t._5.toString 
            var status = "사용중"
            if(t._11 == "true") { status = "해지"}

            //IP수 구하기
            var q2 = Q.query[String,(String)]("SELECT count(*) FROM ips where pcsid = ? group by pcsid")
            var ipnumber = "0"
            val idcount = q2(t._1).list
            if( idcount.size != 0) { ipnumber = idcount(0)}
             sublist = Map("code" -> t._1,
               "name" -> t._2,
               "region" -> t._4.substring(0,2),
               "address" -> addr,
               "phone" -> t._7,
               "mobile" -> t._8,
               "user" -> t._6,
               "ip" -> ipnumber,
               "status" -> status,
               "channel" -> t._9,
               "regdate" -> t._12,
               "games" -> "games"
                )
             returnList += sublist
        }       
      }

	respondJson(Map("data" -> returnList))
  }
}

@GET("getdetails/:code")
class Getdetails extends DefaultLayout {	
  def execute() {
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    var games = Map[Any,Any]()
    var ips = Map[Any,Any]()
    var pcs = Map[Any,Any]()
    var code = param("code")
    val db = forURL()
      db withSession { implicit session =>
    var queryString = "SELECT * FROM PCS WHERE RID = ?"
    var q1 = Q.query[String, (String,String,String,String,String,String,String,String,String,String,String,String)](queryString)
    val peroid = q1(code).list
    if(peroid.size == 0 ){respondJson("okay")}
    else{
        //IP수 구하기
        var t = peroid(0)
            var q2 = Q.query[String,(String)]("SELECT count(*) FROM ips where pcsid = ? group by pcsid")
            var ipnumber = "0"
            val idcount = q2(t._1).list
            if( idcount.size != 0) { ipnumber = idcount(0)}
             var status = "사용중"
            if(t._11 == "true") { status = "해지"}
            var addr = t._4.toString + t._5.toString 
        pcs = Map("code" -> t._1,
               "name" -> t._2,
               "region" -> t._4.substring(0,2),
               "add1" -> t._4,
               "add2" -> t._5,
               "phone" -> t._7,
               "mobile" -> t._8,
               "user" -> t._6,
               "ip" -> ipnumber,
               "status" -> status,
               "channel" -> t._9,
               "regdate" -> t._12,
               "games" -> "games",
               "post" ->t._3
                )
    }
    }
     var iplist = "<tr><th>IP주소</th>"
     iplist += "<th>게임A</th>"
     iplist += "<th>게임B</th>"
     iplist += "<th>게임C</th>"
     iplist += "<th>게임D</th>"
     iplist += "<th>게임E</th>"
     iplist += "</tr>"
     var iplistbody = "<tbody><tr>"
      iplistbody += "<td>192.168.0.1</td>"
     iplistbody += "<td>2015-06-27</td>"
     iplistbody += "<td>2015-06-28</td>"
     iplistbody += "<td>2015-06-29</td>"
     iplistbody += "<td>2015-06-30</td>"
     iplistbody += "<td>2015-06-31</td>"
     iplistbody += "</tr></tbody>"
	respondJson(Map("detail" -> pcs,"iplistheader" -> iplist,"iplistbody"->iplistbody))
  }
}

@GET("getips/:code")
class Getips extends DefaultLayout {    
  def execute() {
    var code = param("code")

    val db = forURL()
    db withSession { implicit session =>  
        var queryString = "SELECT IP FROM ips WHERE RID = ?"
        var q1 = Q.query[String, (String)](queryString)
        val peroid = q1(code).list
        if(peroid.size == 0 ){respondJson("okay")}
        else{
            respondJson(peroid)
        }
    }
}
}

@GET("getgamelist")
class Getgamelist extends DefaultLayout {    
  def execute() {
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    val db = forURL()
    db withSession { implicit session =>  
        var queryString = "SELECT * FROM game WHERE 1 = ?"
        var q1 = Q.query[String, (String,String,String,String,String,String,String,String,String,String,String)](queryString)
        val peroid = q1("1").list
        if(peroid.size == 0 ){respondJson("okay")}
        else{
            for (t <- peroid) {
                var addr = t._6 +" " + t._11
                var idate = t._7 + "~" + t._8
                var istatus = t._9 + " " + t._10
                 sublist = Map("name" -> t._2,
                   "company" -> t._3,
                   "company_number" -> t._4,
                   "owner" -> t._5,
                   "address" -> addr,
                   "install-date" -> idate,
                   "install-status" -> istatus
                    )
                returnList += sublist  
            }
            respondJson(Map("data" -> returnList))
        }
    }
}
}

@GET("getuserlist")
class Getuserlist extends DefaultLayout {    
  def execute() {
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    val db = forURL()
    db withSession { implicit session =>  
        var queryString = "SELECT * FROM users WHERE 1 = ?"
        var q1 = Q.query[String, (String,String,String,String,String,String,String,String,String,String,String,String)](queryString)
        val peroid = q1("1").list
        if(peroid.size == 0 ){respondJson("okay")}
        else{
            for (t <- peroid) {
                 sublist = Map("rid" -> t._1,
                   "name" -> t._4,
                   "position" -> t._5,
                   "company" -> t._6,
                   "email" -> t._7,
                   "phone" -> t._8,
                   "mobile" -> t._9,
                   "work" -> t._10,
                   "role" -> t._11,
                   "last_connect" -> t._12
                    )
                returnList += sublist  
            }
            respondJson(Map("data" -> returnList))  
        }
    }
}
}

// @GET("checkgame/:ip/:dir")
// class Checkgame extends DefaultLayout {    
//   def execute() {
//     val t1 = param("ip")
//     val t2 = param("dir")
//         val pcs: TableQuery[Pcs] = TableQuery[Pcs]
//         val games: TableQuery[Games] = TableQuery[Games]
//     val db = forURL()
//           db withSession { implicit session =>
//             var q2 = users.filter(p => p.ip === ip && p.dir === dir).list
//             if(q2.size > 0) {   isokay = true           }
//           }
//     }
//     }