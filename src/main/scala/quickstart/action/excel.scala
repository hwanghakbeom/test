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
import java.io.FileInputStream
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
	    var userid = session("userId")
	    var patternt = "\\d+".r
	    var regresult = patternt findAllIn userid.toString
	    var llist = regresult.toList
	    var rid = llist(0)
		var regdate = TransDate.getCurrentDate()
		val pcs: TableQuery[Pcs] = TableQuery[Pcs]
		val ips: TableQuery[Ips] = TableQuery[Ips]
		val db = forURL()
		db withSession { implicit session =>

           // def cleandummy(ip: String) = sqlu"delete from dummyexcel where rid > $rid and name = ''".first
           //   var rows= cleandummy("0") 
           //   println(s"Deleted $rows rows")

          var channelquery = "select name from channel where rid = (select work from users where rid = ?)"
          var result = Q.query[String,(String)](channelquery)
          val channelname = result(rid).list
          val channel = channelname(0)

			var excelquery = Q.query[String,(String,String,String,String,String,String)]("SELECT * FROM dummyexcel where 1 = ? and name <> ''")
			val peroid = excelquery("1").list
			for (t <- peroid) {
		  	//pc방 입력
		  	var rid = (pcs returning pcs.map(_.rid)) += Pc(None,t._2, "", t._3, "", "", "", "", channel, "", "",regdate,t._6)
		  	//ip입력
		  	var patternt = "\\d+".r
		  	var arr = t._4.split(".".toArray)
		  	if(arr.size == 4){
		  	var iptext = arr(0) + "." + arr(1) + "." + arr(2)
		  	//endip 가 없음
		  	    if(t._5 != ""){
				  	for(index <- arr(3).toInt to t._5.toInt){
				  		var ip = iptext + "." + index.toString
				  		var queryString = "SELECT ip FROM ips WHERE ip = ?"
				  		var q1 = Q.query[String, (String)](queryString)
				  		val peroid = q1(ip).list
				  		if(peroid.size == 0 )
				  		{
				  			ips += Ip(None,rid.toString, ip , regdate)
				  		}
				  		else
				  		{
				  			ips.filter(_.ip === ip).map(p => (p.pcsid,p.installdate)).update((rid.toString,regdate))
				  		}
				  	}		  	    	
		  	    }
		  	    else{
		  	    	ips += Ip(None,rid.toString, t._4 , regdate)
		  	    }
		  	}


		  }
		  //delete dummyexcel
            // def deletedummy(ip: String) = sqlu"delete from dummyexcel where rid > $rid".first
            //   rows= deletedummy("1") 
            //   println(s"Deleted $rows rows")
		}
		respondJson("okay")
	}
	
}


@POST("excelimport")
class PostExcelimport extends DefaultLayout {	
	def execute() {
		var userid = session("userId")
		var filename = param("filename")
		val whereami = System.getProperty("user.dir")
		var fis:FileInputStream =new FileInputStream(whereami + "/public" + filename);
		var workbook:HSSFWorkbook =new HSSFWorkbook(fis);
		val dummy: TableQuery[Dummyexcels] = TableQuery[Dummyexcels]
		var rowindex=0;
		var columnindex=0;
		var sheet:HSSFSheet = workbook.getSheetAt(0);
		var rows = sheet.getPhysicalNumberOfRows();
		val db = forURL()
		db withSession { implicit session =>
            def deletedummy(rid: String) = sqlu"delete from dummyexcel where rid > $rid".first
              val rows= deletedummy("1") 
              println(s"Deleted $rows rows")			
		}
		for(rowindex <- 1  to sheet.getLastRowNum){
			var row =sheet.getRow(rowindex);
			if(row !=null){
				var cells=row.getPhysicalNumberOfCells();
			//	println(cells)
				var value=""
				var name = ""
				var address = ""
				var startip = ""
				var endip = ""
				var ip = ""
				for(columnindex <- 0 to 2){
					var cell=row.getCell(columnindex);
					if(cell != null){
						if(cell.getCellType() == 0){
							value=cell.getNumericCellValue()+"";
						}
						else if(cell.getCellType() == 1){
							value = cell.getStringCellValue()+"";
						}
						else if(cell.getCellType() == 3){
							value=cell.getBooleanCellValue()+"";
						}
						else if(cell.getCellType() == 5){
							value=cell.getErrorCellValue()+"";
						}
					}
					if(rowindex > 1){
						if(columnindex == 0){
							//이름
							name = value.trim						
						}
						else if(columnindex == 1){
							//아이피
							ip = value.replace(" ","").replace("-","~")					
						}
						else if(columnindex == 2){
							//주소
							address = value					
						}
						// else if(columnindex == 7){
						// 	endip = value.replace(".0","")			
						// }
					}	
				}
				if(name != "" && address != "" && ip.split("~").size > 0) { 
					startip = ip.split("~")(0)
					endip = ip.split("~")(1)

					db withSession { implicit session =>

						if(name != "false"){
							dummy += Dummyexcel(None,name,address,startip,endip,ip)
						}
					}
				 }

			}
		}
		respondJson("okay")
	}
}

