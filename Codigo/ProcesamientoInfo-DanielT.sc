import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder
import scala.collection.immutable.ListMap   // List que me ordena los datos

val path2DataFile = "/home/daniel/MEGA/UTPL/3ciclo/Programacion-funcional-reactiva/CsvLimpio.csv"

val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
case class Tweet(
                  idStr: String,
                  fromUser: String,
                  text: String,
                  createdAt: String,
                  time: LocalDateTime,  //TODO getHour() groupBy(identity) genera hasmap[first 1]
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

// Tratamiento del archivo
implicit val decoder : CellDecoder[LocalDateTime] = localDateTimeDecoder(formatDateTime)
val fuenteDatos = new File(path2DataFile).readCsv[List, Tweet](rfc.withHeader)
val valoresCorrectos = fuenteDatos.collect({ case Right(tweet) => tweet })

// Número de tweets por hora
val horaDeCadaTweet = valoresCorrectos.map(tweet => tweet.time.getHour)
val tweetPorHoraAgrupados = horaDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val tweetPorHoraAgrupadosyOrdenados = ListMap(tweetPorHoraAgrupados.toSeq.sortWith(_._2 > _._2): _* )
// TODO Convertir a JSON Array

// TODO Número de retweets por hora
// TODO Como identifico un retweet?

// Número de tweets por dia
val diaDeCadaTweet = valoresCorrectos.map(tweet => tweet.time.getDayOfMonth)
val tweetsPorDiaAgrupados = diaDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val tweetPorDiaAgrupadosyOrdenados = ListMap(tweetsPorDiaAgrupados.toSeq.sortWith(_._2 > _._2): _* )

// TODO Número de retweets por dia

// TODO Aplicaciones más utilizadas para publicar Tweets

// TODO Distribución de Hashtags
  // TODO Sacar los hashtag del JSON { "Hashtag" , <posición_en_el_texto> }

// TODO Distribución de menciones
  // TODO No esta claro como identificar las menciones

// TODO Distribución de URLs

// TODO Distribución de media (coeficiente parsons)


