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
import io.netty.channel
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import xitrum.action.Net
import io.netty.handler.codec.http.multipart.FileUpload
import java.nio.file.Files.copy
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardCopyOption._
import java.nio.file.FileSystems

@GET("getpcstatus")
class Getpcstatus extends DefaultLayout {	
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
	respondJson(Map("data" -> returnList))
  }
}

@GET("getpcstatus2/:channel/:region/:check/:startdate/:enddate/:condition/:detail")
class Getpcstatus2 extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    var channel = param("channel")
    var region = param("region")
    var check = param("check")
    var startdate = param("startdate")
    var enddate = param("enddate")
    var condition = param("condition")
    var detail = param("detail")

    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var gamelist = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    var queryString = ""
    queryString += "SELECT RID,NAME,ADD1,ADD2,ADD3,OWNER,PHONE,MOBILE,CHANNEL,LASTDATE,ISFINISHED, REGDATE FROM pcs WHERE 1 = ?"
    //queryString += " AND regdate between '" + startdate + "' and '" + enddate + "'"
    if(channel != "채널전체"){ queryString += " AND CHANNEL = '" + channel +"'"}
    if(region != "전체지역") {
        if(region == "서울"){
          queryString += " AND add2 like '" + region +"%'"
        }
        else if(region == "부산"){
          queryString += " AND add2 like '" + region +"%'"
        }
        else if(region == "경남"){
          queryString += " AND substring(ADD2,1,3) in ('경남','경상남')"
        }
        else if(region == "울산"){
          queryString += " AND add2 like '" + region +"%'"
        }    
        else{
          queryString += " AND substring(ADD2,1,3) = '" + region +"'"
        }      
        
      }
    if(condition == "PA" && detail != "") { queryString += " AND NAME = '" + detail +"'" }
    if(condition == "CT" && detail != "") { queryString += " AND OWNER = '" + detail +"'" }
    if(condition == "NY" && detail != "") { queryString += " AND RID = ( SELECT PCSID FROM ips WHERE IP =     '" + detail + "')" }
    println(queryString)
    val db = forURL()
      db withSession { implicit session =>

          //게임별 PC방 코드 구하기
          var patternt = "\\d+".r
          var regresult = patternt findAllIn check
          var llist = regresult.toList

          var gamepc = Q.query[String,(String,String,String)]("SELECT B.pcsid,C.rid, C.name FROM ipgame A, ips B, game C WHERE A.ip = B.ip AND A.game = C.name AND 1 = ?")
          var gamepcperiod = gamepc("1").list
          var gamesublist = Map[Any,Any]()
          for (gt <- gamepcperiod) {
            gamesublist = Map("pc" -> gt._1,
              "gid" -> gt._2,
              "name" -> gt._3)
            gamelist += gamesublist
          }      
        var q1 = Q.query[String, (String, String,String, String,String, String,String, String,String, String,String,String)](queryString)
        val peroid = q1("1").list
        for (t <- peroid) {
            var addr = t._4.toString + t._5.toString 
            var status = "사용중"
            if(t._11 == "true") { status = "해지"}

            //IP수 구하기
            var q2 = Q.query[String,(String)]("SELECT count(*) FROM ips where pcsid = ? group by pcsid")
            var ipnumber = "0"
            var games = ""
            for(index <- 0 to  gamelist.size - 1) {
              if(t._1 == gamelist(index)("pc")) { 
                if( gamelist(index)("name").toString.r.findAllIn(games).length == 0 )
                  games += gamelist(index)("name") + ","
                
            }
          }
            val idcount = q2(t._1).list
            if( idcount.size != 0) { ipnumber = idcount(0)}
              if(!check.startsWith("all") && llist.size == 0) {  //전체게임

                sublist = Map("code" -> t._1,
                   "name" -> t._2,
                   "region" -> t._4.split(" ")(0),
                   "address" -> addr,
                   "phone" -> t._7,
                   "mobile" -> t._8,
                   "user" -> t._6,
                   "ip" -> ipnumber,
                   "status" -> status,
                   "channel" -> t._9,
                   "regdate" -> t._12,
                   "games" -> games
                    )    
                  returnList += sublist             
              }
              else if(llist.size > 0){
                var isexist = false
                for(index <- 0 to  gamelist.size - 1) {
                  if(gamelist(index)("pc")== t._1){
                    for(index2 <- 0 to  llist.size - 1) {
                      if(llist(index2) == gamelist(index)("gid")){
                        isexist = true                           
                      }
                
                    }
                  }
                }
                if(isexist == true) {
                     sublist = Map("code" -> t._1,
                       "name" -> t._2,
                       "region" -> t._4.split(" ")(0),
                       "address" -> addr,
                       "phone" -> t._7,
                       "mobile" -> t._8,
                       "user" -> t._6,
                       "ip" -> ipnumber,
                       "status" -> status,
                       "channel" -> t._9,
                       "regdate" -> t._12,
                       "games" -> games
                        )  
                    returnList += sublist                    
                }

              }
              else{
                if(games == ""){
                    sublist = Map("code" -> t._1,
                       "name" -> t._2,
                       "region" -> t._4.split(" ")(0),
                       "address" -> addr,
                       "phone" -> t._7,
                       "mobile" -> t._8,
                       "user" -> t._6,
                       "ip" -> ipnumber,
                       "status" -> status,
                       "channel" -> t._9,
                       "regdate" -> t._12,
                       "games" -> games
                        )   
                        returnList += sublist     
                        }               
              }


             

        }       
      }

	respondJson(Map("data" -> returnList))
  }
}

@GET("getdetails/:code")
class Getdetails extends DefaultLayout {	
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    var games = Map[Any,Any]()
    var ips = Map[Any,Any]()
    var pcs = Map[Any,Any]()
    var iplist = ""
    var iplistbody = "<tbody><tr>"
    var code = param("code")
    code = code.replace("<a>","")
    code = code.replace("</a>","")
    val db = forURL()
      db withSession { implicit session =>
    var queryString = "SELECT * FROM pcs WHERE RID = ?"
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
               "post" ->t._3,
               "owner" -> t._6
                )
    }
     iplist = "<tr><th>IP주소</th>"


    //game list
    var gameq = Q.query[String,(String,String)]("SELECT rid, name FROM game where 1 = ?")
    var gamepq = gameq("1").list
    for (t <- gamepq) {
      iplist += "<th>"+ t._2 +"</th>"
    }

    //ip list
    var finder = ""
    var ipq = Q.query[String,(String,String)]("SELECT rid,ip FROM ips where pcsid = ?")
    var ipq1 = ipq(code).list
    for (t <- ipq1) {
      iplistbody += "<tr>"
      iplistbody += "<td>" + t._2 + "</td>"
      // 게임등록일 채우기
      var mapp = Q.query[String,(String,String)]("SELECT game,installdate FROM ipgame where ip = ?")  //t._2
      var mappq = mapp(t._1).list
      for (t1 <- gamepq) {  //게임 루프
        finder = ""
        for(t2 <- mappq) {  //매핑 루프
             if(t1._1 == t2._1) { finder = t2._2 }                        
        }
                if(finder != "") { iplistbody += "<td>" + finder + "</td>" }
                else { iplistbody += "<td></td>" }
      }

      iplistbody += "</tr>"
    }

    }
     iplist += "</tr>"
     iplistbody += "</tbody>"
	respondJson(Map("detail" -> pcs,"iplistheader" -> iplist,"iplistbody"->iplistbody))
  }
}

@GET("getips/:code")
class Getips extends DefaultLayout {    
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
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
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
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
                var idate = t._7 + "~" + t._8
                var istatus = t._9 + " " + t._10
                 sublist = Map("rid" -> t._1,
                  "name" -> t._2,
                   "company" -> t._3,
                   "company_number" -> t._4,
                   "owner" -> t._5,
                   "address1" -> t._6,
                   "address2" -> t._11,
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

@GET("getchannellist")
class Getchannellist extends DefaultLayout {    
  def execute() {
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") != "admin") { redirectTo("/installbyg")}
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    val db = forURL()
    db withSession { implicit session =>  
        var queryString = "SELECT * FROM channel where 1 = ?"
        var q1 = Q.query[String, (String,String,String,String,String,String,String,String,String)](queryString)
        val peroid = q1("1").list
        if(peroid.size == 0 ){respondJson("okay")}
        else{
            for (t <- peroid) {
                 sublist = Map("rid" -> t._1,
                  "name" -> t._2,
                   "company" -> t._3,
                   "regnumber" -> t._4,
                   "owner" -> t._5,
                   "address" -> t._6,
                   "regdate" -> t._7,
                   "enddate" -> t._8,
                   "userid" ->t._9
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
    if(session("userId") == "") { redirectTo("/login")}
    if(session("role") == "adv") { redirectTo("/installbyg")}
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
                var roles = ""
                var works = ""
                if(t._11 == "cha"){ 
                  roles = "채널"
                  queryString = "SELECT NAME FROM channel where rid = ?"
                  var q2 = Q.query[String,(String)](queryString)
                  var peroid = q2(t._10).list
                  if(peroid.size > 0 ) { works = peroid(0)}
                }
                else if(t._11 == "adv"){
                  roles = "광고주"
                  queryString = "SELECT NAME FROM game where rid = ?"
                  var q2 = Q.query[String,(String)](queryString)
                  var peroid = q2(t._10).list
                  if(peroid.size > 0 ) { works = peroid(0)}                  
                }
                else if(t._11 == "admin"){
                  roles = "관리자"
                  works = "관리자"
                }

                 sublist = Map("rid" -> t._2,
                   "name" -> t._4,
                   "position" -> t._5,
                   "company" -> t._6,
                   "email" -> t._7,
                   "phone" -> t._8,
                   "mobile" -> t._9,
                   "work" -> works,
                   "role" -> roles,
                   "last_connect" -> t._12
                    )
                
                returnList += sublist  
            }
            respondJson(Map("data" -> returnList))  
        }
    }
}
}

@GET("checkgame/")
class Checkgame extends DefaultLayout {    
  def execute() {

   val host = channel.remoteAddress

    var patternt = "\\d+".r
    var regresult = patternt findAllIn host.toString
    var llist = regresult.toList
    var ip = llist(0) + "." + llist(1) + "." + llist(2) + "." + llist(3)
    val db = forURL()
     val ipnumber: TableQuery[Ipnumbers] = TableQuery[Ipnumbers]
    db withSession { implicit session =>
      var q2 = ipnumber.filter{q => q.ip === ip && q.installdate === TransDate.getCurrentDate() }.list
      if(q2.size == 0){
        ipnumber += Ipnumber(None,ip,TransDate.getCurrentDate())
       // println("insert ip")
      }
      var ipcount = scala.collection.mutable.MutableList[String]()
      var q3 = Q.query[String,(String)]("select game from mapping where channel = ( select channel from pcs where rid = ( select  pcsid from ips where ip = ? ) )")
      val per3 = q3(ip).list
      if(per3.size > 0 )
      {
        for (t <- per3) {
          ipcount += t
        }

      }
      respondJson(ipcount)
    }
  }
}

@GET("checkgame/:games")
class Checkgamewithname extends DefaultLayout {    
  def execute() {
   // /118.37.214.252:53023
   var gamename = param("games")
   val host = channel.remoteAddress

    var patternt = "\\d+".r
    var regresult = patternt findAllIn host.toString
    var llist = regresult.toList
    var ip = llist(0) + "." + llist(1) + "." + llist(2) + "." + llist(3)
        //test
    val db = forURL()
    db withSession { implicit session =>
      var queryString = "select directory,type from mapping where channel = (SELECT name FROM channel where name = (select CHANNEL from pcs where rid = (select PCSID from ips where ip = ? ))) and game = '" + gamename + "'"
      var ipcount = scala.collection.mutable.MutableList[Map[Any,Any]]()
      var sublist = Map[Any,Any]()
      var q3 = Q.query[String,(String,String)](queryString)
      val per3 = q3(ip).list
      if(per3.size > 0 )
      {
        for (t <- per3) {
          sublist =Map("type" -> t._2,
            "directory" -> t._1)
         ipcount += sublist
        }

      }
      respondJson(ipcount)
    }
  }
}

@GET("installgame/:games")
class Installwithname extends DefaultLayout {    
  def execute() {
   // 118.37.214.252:53023
   var gamename = param("games")
   val host = channel.remoteAddress

    var patternt = "\\d+".r
    var regresult = patternt findAllIn host.toString
    var llist = regresult.toList
    var ip = llist(0) + "." + llist(1) + "." + llist(2) + "." + llist(3)
        //test
    val db = forURL()
    val ipgame: TableQuery[Ipgames] = TableQuery[Ipgames]
    val ips: TableQuery[Ips] = TableQuery[Ips]
    db withSession { implicit session =>
      var q2 = ips.filter{q => q.ip === ip}.list
      if(q2.size > 0){
        var test = q2(0).productIterator.toList.zip(List("rid", "pcid"))
        var q1 = ipgame.filter(p => p.ip === ip && p.game === gamename && p.installdate === TransDate.getCurrentDate()).list
        if(q1.size == 0) { 
          ipgame += Ipgame(None,test(1)._1.toString,ip,gamename,TransDate.getCurrentDate(),"false")
        }
      }
    }
    respondJson("okay")
  }
}

@POST("ajax/saveimage")
class AjaxTest extends DefaultLayout {
  def execute() {
    val myFile = param[FileUpload]("file")
    var fileName = ""
    if(myFile.isInMemory() == true){
      fileName = myFile.getFilename()
      try{
        var newFile = new File("public/uploadImages/"+fileName)
        myFile.renameTo(newFile)
        // var targetPath:Path  = FileSystems.getDefault().getPath("public/uploadImages", fileName);
            // copy (myFile.getFile(), targetPath,REPLACE_EXISTING)
      } catch {
        case e: Exception => println("exception caught: " + e);
      }
    }
    else{
      fileName = myFile.getFile().getName().substring(4,myFile.getFile().getName().length)
      try{
        var targetPath:Path  = FileSystems.getDefault().getPath("public/uploadImages", fileName);
            copy (myFile.getFile().toPath(), targetPath,REPLACE_EXISTING)
      } catch {
        case e: Exception => println("exception caught: " + e);
      }
    }


    //println(myFile.getFile().getPath())

    respondJson("/uploadImages/"+fileName)
  }
}

@GET("totalipnumberwogame")
class Totalipnumberwogame extends DefaultLayout {
  def execute() {
    val db = forURL()
    db withSession { implicit session =>
      var queryString = "select count(*) from ipnumber where installdate = ?"
      var q3 = Q.query[String,(String)](queryString)
      val per3 = q3(TransDate.getCurrentDate()).list
      respondJson(per3(0))
    }
    
    }
  }

@GET("ipperchannel")
class Totalipperchannel extends DefaultLayout {
  def execute() {
    val db = forURL()
    var sublist = Map[Any,Any]()
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    db withSession { implicit session =>
      var queryString = "select distinct(installdate) from ipnumber where 1 = ?"
      var secondString = "select C.channel,count(*) as cnt from (select ip from ipnumber where installdate = ? ) A, ips B ,pcs C where A.ip = B.ip and B.pcsid = C.rid  group by C.channel;"
      var q1 = Q.query[String,(String)](queryString)
      val per1 = q1("1").list
        for (t <- per1) {
          println(t)
          var q2 = Q.query[String,(String,String)](secondString)
          val per2 = q2(t).list
          for (t1 <- per2){
            sublist = Map("channel" -> t1._1, "count" -> t1._2, "date" -> t)
            returnList += sublist
          }
        }
      at("value") = returnList
      respondView(Map("type" ->"mustache"))
    }
    
    }
  }

@GET("ipperpc")
class Totalipperpc extends DefaultLayout {
  def execute() {
    val db = forURL()
    var sublist = Map[Any,Any]()
    var returnList = scala.collection.mutable.MutableList[scala.collection.mutable.Map[String,String]]()
    db withSession { implicit session =>
      var queryString = "select distinct(installdate) from ipnumber where 1 = ?"

      var channelcountString = "select count(*) from ( select C.channel as cnt from (select ip, installdate from ipnumber where installdate = ? ) A, ips B ,pcs C where A.ip = B.ip and B.pcsid = C.rid group by C.channel) AA;"
      var channelQueryq1 = Q.query[String,(String)](channelcountString)
      var channelQueryResult = channelQueryq1(TransDate.getCurrentDate()).list
      at("channelCount") = channelQueryResult(0)

      var channelTotalCountString = "select count(*) from channel where 1 = ?"
      var channelTotalQueryQ1 = Q.query[String,(String)](channelTotalCountString)
      var channelTotalQueryResult = channelTotalQueryQ1("1").list
      at("channelTotalCount") = channelTotalQueryResult(0)

      var pcCountString = "select count(*) from ( select C.name from (select ip from ipnumber where installdate = ? ) A, ips B ,pcs C where A.ip = B.ip and B.pcsid = C.rid  group by C.name ) AA "
      var pcCountQueryQ1 = Q.query[String,(String)](pcCountString)
      var pcCountResult = pcCountQueryQ1(TransDate.getCurrentDate()).list
      at("pcusingcount") = pcCountResult(0)

      var pcTotalcountString = "select count(*) from pcs where 1 = ?"
      var pcTotalQuery1 = Q.query[String,(String)](pcTotalcountString)
      var pcTotalQueryResult = pcTotalQuery1("1").list
      at("pcTotalcount") = pcTotalQueryResult(0)

      var ipCountString = "select count(ip) from ipnumber where installdate = ?"
      var ipCountQuery1 = Q.query[String,(String)](ipCountString)
      var ipCountResult = ipCountQuery1(TransDate.getCurrentDate()).list
      at("ipcount") = ipCountResult(0)

      var ipTotalCountString = "select count(*) from ips where 1 = ?"
      var ipTotalCountQuery1 = Q.query[String,(String)](ipTotalCountString)
      var ipTotalResult = ipTotalCountQuery1("1").list
      at("ipTotalCount") = ipTotalResult(0)

      var dateString = "select distinct(installdate) from ipnumber where 1 = ?"
      var dateQuery = Q.query[String,(String)](dateString)
      var dateResult = dateQuery("1").list
      for (t <- dateResult) {
        var channelListString = "select name, IFNULL(c2.cnt, 0) as cnt from channel c1 left join ( select C.channel,count(*) as cnt from (select ip from ipnumber where installdate = ? ) A, ips B ,pcs C where A.ip = B.ip and B.pcsid = C.rid  group by C.channel) c2 on c1.name = c2.channel order by name;"
        var channelListQuery = Q.query[String,(String,String)](channelListString)
        var channelListResult = channelListQuery(t).list
        var indexes = 0
        val map = scala.collection.mutable.Map[String,String]()
        map("date") =  t
        for (t1 <- channelListResult){
            map(indexes.toString) = t1._2
            indexes = indexes + 1
        }
        //sublist + Map("date" -> t.substring(5,10))
        returnList += map
      }
      at("value") = returnList

      respondView(Map("type" ->"mustache"))
    }
    
    }
  }
@GET("ipperpcdetail/:datevalue/:channelvalue")
class IpperpcDetail extends DefaultLayout {
  def execute() {
    var datevalue = param("datevalue")
    var channelvalue = param("channelvalue")
    var returnList = scala.collection.mutable.MutableList[Map[Any,Any]]()
    var sublist = Map[Any,Any]()
    val db = forURL()
    db withSession { implicit session =>
      var queryString = "select p.name, AA.cnt from pcs p left join ( "
          queryString += "select C.rid ,C.name,count(*) as cnt from (select ip from ipnumber where installdate = ? ) A, ips B ,pcs C where A.ip = B.ip and B.pcsid = C.rid  and C.channel = '" + channelvalue + "' group by C.name "
          queryString += " ) AA on p.rid = AA.rid "
          queryString += " where p.channel = '" +channelvalue + "'"
      var countQuery = Q.query[String,(String,String)](queryString)
      var dateResult = countQuery(datevalue).list
      for (t <- dateResult) {
        sublist = Map("name" -> t._1, "count" -> t._2)
        returnList += sublist
      }     
    }
    at("value") = returnList
    respondView(Map("type" ->"mustache"))
  }
}


@POST("ipsite/")
class Iptitle extends DefaultLayout {    
  def execute() {
   // /118.37.214.252:53023
   var sitename = param("site")
   val host = channel.remoteAddress

    var patternt = "\\d+".r
    var regresult = patternt findAllIn host.toString
    var llist = regresult.toList
    var ip = llist(0) + "." + llist(1) + "." + llist(2) + "." + llist(3)
        //test
    val db = forURL()
    val ipsite: TableQuery[Ipandsites] = TableQuery[Ipandsites]
    db withSession { implicit session =>
      ipsite += Ipandsite(None,ip,sitename)
    }
    respondJson("okay")
  }
}