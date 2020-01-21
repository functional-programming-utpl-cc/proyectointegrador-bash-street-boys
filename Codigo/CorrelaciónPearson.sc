import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder
val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
val path2DataFile = "C:\\Users\\harry\\Documents\\David\\PF\\ods_2_3.csv"

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

val list= values.collect( { case tweet => (tweet.userFollowersCount,tweet.userFriendsCount) })

def calPearson (tabla: List[(Double, Double)] ): Double ={
  val mediaX= (tabla. map(a=> a._1).sum.toDouble/tabla.length)
  val mediaY= (tabla. map(a=> a._2).sum.toDouble/tabla.length)
  val covarianza = ((tabla. map(a=> a._1 *a._2).sum )/ tabla.length) -(mediaX *mediaY)
  val desX= Math.sqrt((tabla. map(x=> Math.pow(x._1,2)).sum/ tabla.length)- Math.pow(mediaX,2))
  val desY =Math.sqrt((tabla. map(y=> Math.pow(y._2,2)).sum/ tabla.length)- Math.pow(mediaY,2))
  covarianza/(desX*desY)
}
calPearson(list)
val resultado= if(calPearson(list) <1 && calPearson(list) >0) {println("Si exite relación entre seguidores y amigos")}
else {println("No exite relación entre seguidores y amigos")}