import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.matching.Regex// librería para poder usar expresiones regulares
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder

import scala.collection.immutable.ListMap

val path2DataFile = "C:\\Users\\harry\\Documents\\David\\PF\\ods_2_3.csv"
val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
//val pattern = new Regex("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:\\/~\\+#]*[\\w\\-\\@?^=%&amp;\\/~\\+#])?)")
// expresion regular para encontrar url
val pattern = new Regex(">.\\w*\\s\\w*\\s\\w*<")

case class Tweet(
                  idStr: String,
                  fromUser: String,
                  text: String,
                  createdAt: String,
                  time: LocalDateTime,
                  geoCoordinates: String,
                  userLang: String,
                  inReply2UserId: String,
                  inReply2ScreenName: String,
                  fromUserId: String,
                  inReply2StatusId: String,
                  source: String,
                  profileImageURL: String,
                  userFollowersCount: Either[String, Int],
                  userFriendsCount: Either[String, Int],
                  userLocation: String,
                  statusURL: String,
                  entitiesURL: String
                )
implicit val decoder : CellDecoder[LocalDateTime] = localDateTimeDecoder(formatDateTime)
val dataSource = new File(path2DataFile).readCsv[List, Tweet](rfc.withHeader)
val values = dataSource.collect({ case Right(tweet) => tweet })
val fromUsersList = values.map(tweet => tweet.fromUser)
println(fromUsersList.toSet.size)

//devuelve una lista con los datos del campo source
val fromSourceList = values.map(tweet=> tweet.source)
val extraccionPWfromSource = fromSourceList.map(x=> pattern.findFirstIn(x))
val desnenvolver:PartialFunction[Any, String] = { case Some(value:String) => value.tail.init}
val namesPagesWebs=extraccionPWfromSource.collect(desnenvolver)
val namesAgrupados =namesPagesWebs.groupBy(identity).map( { case (k,v) => (k,v.length)} )
val namesAOrdenados= ListMap(namesAgrupados.toSeq.sortWith(_._2>_._2): _*)
