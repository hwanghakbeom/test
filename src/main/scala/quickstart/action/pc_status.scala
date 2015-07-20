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

@GET("pc")
class Pcstatus extends DefaultLayout {	
  def execute() {
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var channelList = scala.collection.mutable.MutableList[Map[Any,Any]]()
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
    var userid = session("userId") 
    val channel: TableQuery[Channels] = TableQuery[Channels]
    val db = forURL()
    db withSession { implicit session =>
            var q1 = channel.filter(_.user === userid.toString).list
            val querysize = q1.size - 1
             for(   index <-0 to querysize ){
                var test = q1(index).productIterator.toList.zip(List("rid", "name", "user"))
                var channelsublist = Map[Any,Any]()
                channelsublist = Map(
                  test(0)._2 -> test(0)._1,
                  test(1)._2 -> test(1)._1
                  )
                  channelList += channelsublist
             }
        }
        at("channel") = channelList
	respondView(Map("type" ->"mustache"))
  }
}