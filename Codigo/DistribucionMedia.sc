import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex// librería para poder usar expresiones regulares
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder
import scala.collection.immutable.ListMap//  library for to use structure ListMap

val path2DataFile = "C:\\Users\\harry\\Documents\\David\\PF\\CsvSinDuplicados.csv"
val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
val patternURL = new Regex("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:\\/~\\+#]*[\\w\\-\\@?^=%&amp;\\/~\\+#])?)")
// expresion regular para encontrar url

val pattern = new Regex(">\\w*\\s\\w*\\s\\w*<")

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
                  userFollowersCount:  Double,
                  userFriendsCount: Double,
                  userLocation: String,
                  statusURL: String,
                  entitiesStr: String
                )
implicit val decoder : CellDecoder[LocalDateTime] = localDateTimeDecoder(formatDateTime)
val dataSource = new File(path2DataFile).readCsv[List, Tweet](rfc.withHeader)
val values = dataSource.collect({ case Right(tweet) => tweet })

import scala.util.{Try,Success,Failure}
def datosExcp(t:Tweet) = Try {ujson.read(t.entitiesStr).obj("media").arr.length}
val distMedia = values.map(tweet => datosExcp(tweet) match {case Success(v) =>v case Failure(e)=>0})
  .groupBy(identity).map ({ case (k,v)=> (k,v.length)})

val out= java.io.File.createTempFile("distribucionmedia", ".csv")
val writer =out.asCsvWriter[ ( Int,Int)] (rfc.withHeader("nromedia", "nrotweet"))
distMedia.foreach(writer.write(_))
writer.close()