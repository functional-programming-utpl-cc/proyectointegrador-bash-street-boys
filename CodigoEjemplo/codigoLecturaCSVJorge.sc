import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder
val path2DataFile = "/Users/jorgaf/Proyectos/tablesawdata/ods.csv"
val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
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
