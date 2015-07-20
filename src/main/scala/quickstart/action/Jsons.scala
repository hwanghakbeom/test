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
    println(channel)
    println(region)
    println(check1)
    println(check2)
    println(check3)
    println(check4)
    println(startdate)
    println(check5)
    println(enddate)
    println(condition)
    println(detail)
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
     sublist = Map("code"->"12347",
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

@GET("getdetails/:code")
class Getdetails extends DefaultLayout {	
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
	respondJson(Map("detail" -> sublist,"iplistheader" -> iplist,"iplistbody"->iplistbody))
  }
}

@GET("getips/:code")
class Getips extends DefaultLayout {    
  def execute() {
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var games = Map[Any,Any]()
    var ips = Map[Any,Any]()
    }
    }