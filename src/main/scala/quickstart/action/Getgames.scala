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

@GET("getgames")
class Getgames extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    // After login success
	redirectTo("pc#games")
  }
}
@GET("getchannel")
class Getchannel extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    // After login success
	redirectTo("pc#channels")
  }
}
@GET("getstatus")
class Getstatus extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
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
    at("value") = returnList
	redirectTo("pc#status")
  }
}