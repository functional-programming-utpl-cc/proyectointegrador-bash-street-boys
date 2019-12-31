import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder

import scala.collection.immutable.ListMap // List que me ordena los datos

// import spray.json._
val path2DataFile = "/home/daniel/Descargas/CsvLimpio-csv.csv"
// val path2DataFile = "/home/daniel/MEGA/UTPL/3ciclo/proyectointegrador-bash-street-boys/Material/CsvLimpio.csv"

val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
case class Tweet(
                  idStr: String,
                  fromUser: String,
                  text: String,
                  createdAt: String,
                  time: LocalDateTime, //TODO getHour() groupBy(identity) genera hasmap[first 1]
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
                  entitiesJson: String // TODO Intenetemos que este sea un formato JSON desde aqui
                )


// Tratamiento del archivo
implicit val decoder : CellDecoder[LocalDateTime] = localDateTimeDecoder(formatDateTime)
val fuenteDatos = new File(path2DataFile).readCsv[List, Tweet](rfc.withHeader)
val valoresCorrectos = fuenteDatos.collect({ case Right(tweet) => tweet })



val numHashtagsPorTweet = valoresCorrectos.map(tweet => ujson.read(tweet.entitiesJson).obj("hashtags").arr.length )
  .groupBy(identity)
  .map({case (k, v) => (k, v.length) } )

val out = java.io.File.createTempFile("DistribucionHashtags", ".csv")
val writer = out.asCsvWriter[(Int, Int)] (rfc.withHeader("NumHashtags", "count"))
numHashtagsPorTweet.foreach(writer.write(_) )                         // Guarda el ultimo map
writer.close()


val temp = valoresCorrectos.map(tweet => ujson.read(tweet.entitiesJson).obj("hashtags").arr.length )
  .groupBy(identity)
  .map({case (k, v) => (k, v.length) } )


// Lugar de origen de los tweets
val lugarDeCadaTweet = valoresCorrectos.map(tweet => tweet.userLocation)
val lugaresAgrupados = lugarDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val lugaresFiltrados = lugaresAgrupados.filter(location => location._2 > 10)
val lugaresAgrupadosyOrdenados = ListMap(lugaresFiltrados.toSeq.sortWith(_._2 > _._2): _* )

// Crear archivo
val out = java.io.File.createTempFile("origenDelTweet", ".csv")
val writer = out.asCsvWriter[(String, Int)] (rfc.withHeader("location", "count"))
lugaresAgrupadosyOrdenados.foreach(writer.write(_) )                         // Guarda el ultimo map
writer.close()

// Número de tweets por hora
/*
val horaDeCadaTweet = valoresCorrectos.map(tweet => tweet.time.getHour)
val tweetPorHoraAgrupados = horaDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val tweetPorHoraAgrupadosyOrdenados = ListMap(tweetPorHoraAgrupados.toSeq.sortWith(_._2 > _._2): _* )
*/
// Crear archivo
/*
val out = java.io.File.createTempFile("tweetsPorHora", ".csv")
val writer = out.asCsvWriter[(Int, Int)] (rfc.withHeader("hour", "count"))
tweetPorHoraAgrupadosyOrdenados.foreach(writer.write(_) )                         // Guarda el ultimo map
writer.close()
*/
/*
// Número de retweets por hora
val retweetsFiltrados = valoresCorrectos.filter(tweet => tweet.text.startsWith("RT") || tweet.text.startsWith("Retweet") )
var retweetsOrdenadosPorHora = retweetsFiltrados.map(tweet => tweet.time.getHour)
val retweetsOrdenadosAgrupadosEnOrdenNat = retweetsOrdenadosPorHora.groupBy(identity).map( { case (k, v) => (k, v.length) } )
*/
// val temp = ListMap(retweetsOrdenadosAgrupadosEnOrdenNat.toSeq.sortWith(_._2 > _._2): _* )


  // Crear archivo

/*val out = java.io.File.createTempFile("retweetsPorHora", ".csv")
val writer = out.asCsvWriter[(Int, Int)] (rfc.withHeader("hora", "count"))
retweetsOrdenadosAgrupadosEnOrdenNat.foreach(writer.write(_) )
writer.close()
*/
// Número de tweets por dia
val diaDeCadaTweet = valoresCorrectos.map(tweet => tweet.time.getDayOfMonth)
val tweetsPorDiaAgrupados = diaDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val tweetPorDiaAgrupadosyOrdenados = ListMap(tweetsPorDiaAgrupados.toSeq.sortWith(_._2 > _._2): _* )

  // Crear archivo
val out = java.io.File.createTempFile("tweetsPorDia", ".csv")
val writer = out.asCsvWriter[(Int, Int)] (rfc.withHeader("dia", "count"))
tweetPorDiaAgrupadosyOrdenados.foreach(writer.write(_) )
writer.close()

// Número de retweets por dia
val retweetsFiltrados = valoresCorrectos.filter(tweet => tweet.text.startsWith("RT") || tweet.text.startsWith("Retweet") )
var retweetsOrdenadosPorDia = retweetsFiltrados.map(tweet => tweet.time.getDayOfMonth)
val retweetsOrdenadosAgrupadosEnOrdenNat = retweetsOrdenadosPorDia.groupBy(identity).map( { case (k, v) => (k, v.length) } )
// val temp = ListMap(retweetsOrdenadosAgrupadosEnOrdenNat.toSeq.sortWith(_._2 > _._2): _* )

  // Crear archivo
val out = java.io.File.createTempFile("retweetsPorDia", ".csv")
val writer = out.asCsvWriter[(Int, Int)] (rfc.withHeader("dia", "count"))
retweetsOrdenadosAgrupadosEnOrdenNat.foreach(writer.write(_) )
writer.close()

// TODO Aplicaciones más utilizadas para publicar Tweets
val appsDeCadaTweet = valoresCorrectos.map(tweet => tweet.source)
val appsAgrupadas = appsDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val appsAgrupadosyOrdenados = ListMap(appsAgrupadas.toSeq.sortWith(_._2 > _._2): _* )

// TODO Distribución de Hashtags
  // TODO Sacar los hashtag del JSON { "Hashtag" , <posición_en_el_texto> }

// import MyJsonProtocol._
// import spray.json._
import upickle.default._



case class infoAdicional(hashtags : String, symbols: String, user_mentions: String, urls: String)

object MyProtocoloJson extends DefaultJsonProtocol{
  implicit val miCampo = jsonFormat4(infoAdicional.apply)
}

val distribucionHashtagsPorTweet = valoresCorrectos.map(tweet => tweet.toJson)
val hashtagsPorTweet = distribucionHashtagsPorTweet.map(jsonDelTweet => jsonDelTweet.convertTo[infoAdicional]
// TODO AQui estrabamos

// TODO Distribución de menciones
  // TODO No esta claro como identificar las menciones

// TODO Distribución de URLs

// TODO Distribución de media (coeficiente parsons)


