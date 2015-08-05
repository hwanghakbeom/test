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
import java.nio.file.Files.copy
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardCopyOption._
import java.nio.file.FileSystems
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.ByteArrayInputStream
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.DateUtil
import scala.collection.JavaConversions._

@GET("excelimport")
class Excelimport extends DefaultLayout {	
	def execute() {
		var regdate = TransDate.getCurrentDate()
		val pcs: TableQuery[Pcs] = TableQuery[Pcs]
		val ips: TableQuery[Ips] = TableQuery[Ips]
		val db = forURL()
		db withSession { implicit session =>
			var excelquery = Q.query[String,(String,String,String,String,String)]("SELECT * FROM dummyexcel where 1 = ?")
			val peroid = excelquery("1").list
			for (t <- peroid) {
		  	//pc방 입력
		  	var rid = (pcs returning pcs.map(_.rid)) += Pc(None,t._2, "", t._3, "", "", "", "", "http", "", "",regdate)

		  	//ip입력
		  	var patternt = "\\d+".r
		  	var arr = t._4.split(".".toArray)
		  	var iptext = arr(0) + "." + arr(1) + "." + arr(2)
		  	for(index <- arr(3).toInt to t._5.toInt){
		  		var ip = iptext + "." + index.toString
		  		var queryString = "SELECT ip FROM ips WHERE ip = ?"
		  		var q1 = Q.query[String, (String)](queryString)
		  		val peroid = q1(ip).list
		  		if(peroid.size > 0 )
		  		{
		  			println("중복아이피" + ip) 
		  		}
		  		else
		  		{
		  			ips += Ip(None,rid.toString, ip , regdate)
		  		}
		  	}
		  }
		}
		respondJson("okay")
	}
	
}



