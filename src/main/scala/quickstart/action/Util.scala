package object quickstart {

import org.joda.time.{DateTime, Period, LocalDate}
import org.joda.time.Chronology
import org.joda.time.chrono
import org.joda.time.format._
import scala.slick.driver.MySQLDriver.simple._ 

 val USERNAME="root"
 val PASSWORD="1234"
 val URL="jdbc:mysql://localhost:3306/pcs"
 val DRIVER="org.mariadb.jdbc.Driver"
 

 
 def forURL()=
   Database.forURL(URL, user=USERNAME,password=PASSWORD,driver = DRIVER)

object TransDate {
		def getCurrentDate(): String = {
		val ctime = new DateTime()
		var time  = ctime.getYear().toString +"-"+ ctime.getMonthOfYear() +"-"+ ctime.getDayOfMonth().toString
		var dtf:DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		var jodatime:DateTime = dtf.parseDateTime(time);
		var dtfOut:DateTimeFormatter  = DateTimeFormat.forPattern("yyyy-MM-dd");
		var temp = dtfOut.print(jodatime)
		return temp

	}
		def getCurrentTime(): String = {
		val ctime = new DateTime()
		val pattern:String  = "hh:mm:ss"
		val formatter:DateTimeFormatter  = DateTimeFormat.forPattern(pattern)
		val returnString:String  = formatter.print(ctime)
		//val returnString = ctime.getHourOfDay().toString +":"+ ctime.getMinuteOfHour().toString +":"+ ctime.getSecondOfMinute().toString
		return  returnString
	}
}
}



