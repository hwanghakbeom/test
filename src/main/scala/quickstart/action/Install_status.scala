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

@GET("installbyg")
class Installbyg extends DefaultLayout {	
  def execute() {
    // After login success
    var p1 = paramo("select-region")
    at("test") = p1
	respondView(Map("type" ->"mustache"))
  }
}
@GET("installbyc")
class Installbyc extends DefaultLayout {	
  def execute() {
    // After login success
	respondView(Map("type" ->"mustache"))
  }
}
@GET("installbycparam")
class Installbycparam extends DefaultLayout {	
  def execute() {
    // After login success
	forwardTo[Installbyc]()
  }
}