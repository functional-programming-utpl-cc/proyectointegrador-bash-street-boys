/* Las librerias que utilizadas en el proyecto se las puede encontrar en https://mvnrepository.com/
las cuales se  importan en nustro archivo sbt. Ahora bien nuestro libreria java.io.File la utilizamos para pasar el path,
es decir la direccion de donde se encuentra nuestro archivo csv proporcionado para nuestro proyecto, luego tenemos las librerias
LocalDateTime y DateTimeFormatter que usamos para dar el tipo de dato fecha dias/mes/anio y su uso
adecuado,como siguiente importamos la libreria Regex para hacer uso de las expresiones regulares
mediante la instaciacion de un objeto Regex ,tambien tenemos la importacion de Kantan para el tratamiento
adecuado de nuestro csv, ademas usamos localDateTimeDecoder para poder usar el decodificador para elegir que formato de fecha
vamos usar, finalmente tenemos ListMap para poder crear una lista que contengas la estructura de mapas (clave-> valor).
*/

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.matching.Regex// librería para poder usar expresiones regulares
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import kantan.csv.java8.localDateTimeDecoder
import scala.collection.immutable.ListMap

// Una de las caracteristica de la progrmacion funcional es que los datos son inmutables y se los crean con la palabra reservada val
//que usura frecuentemente en adelante

// Creamos una una variable que recibe la direccion de nuestro archivo csv
val path2DataFile = "C:\\Users\\harry\\Documents\\David\\PF\\datasetTweetsSinSaltosDeLinea.csv"

// Creamos la la variable formatDateTime que es instanciada por DateTimeFormatter para especificarel formato de fecha
val formatDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
// Creamos la la variable  patternURL que recibe la expresion regular para encontrar url
val patternURL = new Regex("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:\\/~\\+#]*[\\w\\-\\@?^=%&amp;\\/~\\+#])?)")

// Creamos la la variable  patternURL que recibe la expresion regular para encontrar url
val pattern = new Regex(">.\\w*\\s\\w*\\s\\w*<")

/* En el siguiente bloque de codigo creamos el objeto Tweet con 18 atributos que representan nuestran 18 columnas del csv, con el
fin de manipular con mayor facilidad nuestros datos, como e vera mas adelate en nuestro codigo el uso de tweet.text . */

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
                  userFollowersCount: Double,
                  userFriendsCount: Double,
                  userLocation: String,
                  statusURL: String,
                  entitiesJson: String
                )

/* Creamos la variable decoder del tipo de dato CellDecoder[LocalDateTime] instanciadando  localDateTimeDecoder para facilitar y
dar el formato a la fecha. Y ahora se puede codificar facilmente dats que contienen instancias de LocalDateTime*/
implicit val decoder : CellDecoder[LocalDateTime] = localDateTimeDecoder(formatDateTime)

/*Creamos la variable dataSource donde usamos un contructor de tipo File para pasar el path de nuestro csv y luego utilizar la
funcion .readCsv en el cual se le da la estructura que sea una lista con el formato de nuestro objeto Tweet y que tenga ese
  encabezado*/
val dataSource = new File(path2DataFile).readCsv[List, Tweet](rfc.withHeader)

/*Damos el nombre values a nuestro datos capturados unicamente que sean correctos  case Right(tweet) y que seran devueltos
dentro de la funcion collect que nos permite pasar funciones paraciales donde el case omite los datos que no sean Rigth */
val values = dataSource.collect({ case Right(tweet) => tweet })

// Creamos fromUsersList variable que contiene lo datos mapeados de values que es un atributo del objeto tweet denominado fromUser
val fromUsersList = values.map(tweet => tweet.fromUser)

// la siguiente linea nos da como resultado el numero de usuarios sin repetirse
println(fromUsersList.toSet.size)



/* Distribucion de hastags
En la siguiente linea mapeamos la colecion values donde nuestro atributo tweet.entitiesJson nesecita tratamiento especial ya que es
 una texto sencillo que sirve para el intercambios de datos hacemos uso de nuestra libreria upickle para navergar en la estructura del json
 ,entonces porcedemos a leerlo como tal json.read(tweet.entitiesJson) dada la nomeclatura de la libreria especificamos que es un objeto con
  el nombre de hashtags y que posse arreglos y queremos su longuitud*/

val numHashtagsPorTweet = values.map(tweet => ujson.read(tweet.entitiesJson).obj("hashtags").arr.length )
  .groupBy(identity)
  .map({case (k, v) => (k, v.length) } )

/* Aqui podemos ver el uso de la libreria java.io.File que nos permite la creación de un archivo temporal
que recibe dos parametros el nombre "DistribucionHashtags" y su extensión ".csv", luego especificamos que tipos
de datos vamos a escribir en dicho archivo y con que cabecera queremos que se genere
 out.asCsvWriter[(Int, Int)] (rfc.withHeader("NumHashtags", "count")).
 Y de manera similar se crean los archivos temporales para los diferentes pedidos en el proyecto*/

val out = java.io.File.createTempFile("DistribucionHashtags", ".csv")
val writer = out.asCsvWriter[(Int, Int)] (rfc.withHeader("NumHashtags", "count"))
numHashtagsPorTweet.foreach(writer.write(_) )                         // Guarda el ultimo map
writer.close()

// Lugar de origen de los tweets
/*  de igual manera antes vista se crean varables cn las que identificamos que vamos a mapear una lista con el atributo userLocation
* para agruparlas en mpas y con la longuitud de su agrupacion se obtiene la cantidad total de dichos datos agrupados , luego usamos la funcion filter
* que recibe como argumento una funcion parcial que  aplica una condicion  generando valores true o false dependiendo de lo que nesecitemos y luego se
* ordena nuestra ListMap*/
val lugarDeCadaTweet = values.map(tweet => tweet.userLocation)
val lugaresAgrupados = lugarDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val lugaresFiltrados = lugaresAgrupados.filter(location => location._2 > 10)
val lugaresAgrupadosyOrdenados = ListMap(lugaresFiltrados.toSeq.sortWith(_._2 > _._2): _* )

// Crear archivo
val out0 = java.io.File.createTempFile("origenDelTweet", ".csv")
val writer0 = out0.asCsvWriter[(String, Int)] (rfc.withHeader("location", "count"))
lugaresAgrupadosyOrdenados.foreach(writer0.write(_) )                         // Guarda el ultimo map
writer0.close()

// Número de tweets por hora

val horaDeCadaTweet = values.map(tweet => tweet.time.getHour)
val tweetPorHoraAgrupados = horaDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val tweetPorHoraAgrupadosyOrdenados = ListMap(tweetPorHoraAgrupados.toSeq.sortWith(_._2 > _._2): _* )

// Crear archivo

val out5 = java.io.File.createTempFile("tweetsPorHora", ".csv")
val writer5= out5.asCsvWriter[(Int, Int)] (rfc.withHeader("hour", "count"))
tweetPorHoraAgrupadosyOrdenados.foreach(writer5.write(_) )                         // Guarda el ultimo map
writer5.close()

// Número de retweets por hora
val retweetsFiltrados1 = values.filter(tweet => tweet.text.startsWith("RT") || tweet.text.startsWith("Retweet") )
var retweetsOrdenadosPorHora = retweetsFiltrados1.map(tweet => tweet.time.getHour)
//retweetsOrdenadosAgrupadosEnOrdenNat ==> abc
val abc = retweetsOrdenadosPorHora.groupBy(identity).map( { case (k, v) => (k, v.length) } )

// Crear archivo

val out6 = java.io.File.createTempFile("retweetsPorHora", ".csv")
val writer6 = out6.asCsvWriter[(Int, Int)] (rfc.withHeader("hora", "count"))

abc.foreach(writer6.write(_) )
writer.close()

// Número de tweets por dia
val diaDeCadaTweet = values.map(tweet => tweet.time.getDayOfMonth)
val tweetsPorDiaAgrupados = diaDeCadaTweet.groupBy(identity).map( { case (k, v) => (k, v.length) } )
val tweetPorDiaAgrupadosyOrdenados = ListMap(tweetsPorDiaAgrupados.toSeq.sortWith(_._2 > _._2): _* )

// Crear archivo
val out1 = java.io.File.createTempFile("tweetsPorDia", ".csv")
val writer1 = out1.asCsvWriter[(Int, Int)] (rfc.withHeader("dia", "count"))
tweetPorDiaAgrupadosyOrdenados.foreach(writer1.write(_) )
writer1.close()

// Número de retweets por dia
val retweetsFiltrados = values.filter(tweet => tweet.text.startsWith("RT") || tweet.text.startsWith("Retweet") )
var retweetsOrdenadosPorDia = retweetsFiltrados.map(tweet => tweet.time.getDayOfMonth)
val retweetsOrdenadosAgrupadosEnOrdenNat = retweetsOrdenadosPorDia.groupBy(identity).map( { case (k, v) => (k, v.length) } )
// val temp = ListMap(retweetsOrdenadosAgrupadosEnOrdenNat.toSeq.sortWith(_._2 > _._2): _* )

// Crear archivo
val out2= java.io.File.createTempFile("retweetsPorDia", ".csv")
val writer2 = out2.asCsvWriter[(Int, Int)] (rfc.withHeader("dia", "count"))
retweetsOrdenadosAgrupadosEnOrdenNat.foreach(writer2.write(_) )
writer2.close()

// DISTRIBUCIÓN DE LAS PÁGINAS MÁS USADAS
/* En el siguiente bloque de codigo se aprecia la distribucion de paginas web's mas usadas de tal manera que al mapear a values
que la primera linea devuelve una lista con los datos del campo source a la cual se les extrae el nombre de las paginas con la
funcion parcial que busca el primer patron en la cadena pattern.findFirstIn(x) , dicho patron es la expresion regular que antes
mencionamos para los nombres de las paginas, luego creamos una funcion parcial desnenvolver que utilizamos para extraer unicamente
nuestros datos correctos Some(value:String) => value.tail.init a los cuales les quitamos el primer y utlimo caracter ya que nuestra
expresion regular nos devuelve por ejemplo >Twiter for Android<*, a continuacion se hace uso de la funcion parcial para tener los
nombres de las paginas totalmente limpios y empezaar a agruparlos  mediante el metodo .groupBy(identity).map({case (k,v)=>(k,v.length)})
propio de collections como es nuestra lista namesPagesWebs al cual se le psa el argumento identity que nos permite reunir datos con
el mismo nombre y final mente los mapeamos con la estructura de un mapa solicitando que el el v.length nos proporcione la cantidad
de veces que se uso ese aplicacion web, finalmente creamos una lista de mapas la cual la convertimos en una secuencia para ordenarla
 por el segundo elemento del mapa; es decir ordenar la lista de mapas por la que pasea mayor valor namesAgrupados.toSeq.sortWith(_._2>_._2) */

val fromSourceList = values.map(tweet=> tweet.source)
val extraccionPWfromSource = fromSourceList.map(x=> pattern.findFirstIn(x))
val desnenvolver:PartialFunction[Any, String] = { case Some(value:String) => value.tail.init}
val namesPagesWebs=extraccionPWfromSource.collect(desnenvolver)
val namesAgrupados =namesPagesWebs.groupBy(identity).map( { case (k,v) => (k,v.length)} )
val namesAOrdenados= ListMap(namesAgrupados.toSeq.sortWith(_._2>_._2): _*)

// De manera similar y sin perdida de generalidad se genera el codigo para la distribucion de los url's

val fromSourceList2 = values.map(tweet=> tweet.source)
val extraccionURLS = fromSourceList2.map(x=> patternURL.findFirstIn(x))
def desenvolver2:PartialFunction[Any, String] = { case Some(value:String) => value}
val urls =extraccionURLS.collect(desenvolver2)
val namesURLS =urls.groupBy(identity).map( { case (k,v) => (k,v.length)} )
val urlsOrdenados = ListMap(namesURLS.toSeq.sortWith(_._2>_._2): _*)

  // DISTRIBUCIÓN DE MEDIA

/* importamos la libreria java.util que nos perite hacer uso de la captura de datos median Try y nos da coo resultado Success que son datos correctos
y Failure que son datos considerados como
Creamos la  funcion datosExcp  para concocer la cantidad de media de cada tweet navegando en la estructura del Json
mapeamos con nuestra funcion datos Excp donde desenvolvemos los datos correctos con Succes y pasamos 0 para el caso de Failure para finalmente agruparlos*/
import scala.util.{Try,Success,Failure}
def datosExcp(t:Tweet) = Try {ujson.read(t.entitiesJson).obj("media").arr.length}
val distMedia = values.map(tweet => datosExcp(tweet) match {case Success(v) =>v case Failure(e)=>0})
  .groupBy(identity).map ({ case (k,v)=> (k,v.length)})

val out3= java.io.File.createTempFile("distribucionmedia", ".csv")
val writer3 =out3.asCsvWriter[ ( Int,Int)] (rfc.withHeader("nromedia", "nrotweet"))
distMedia.foreach(writer3.write(_))
writer3.close()

/*¿Existe una correlación entre el número de amigos y la cantidad de seguidores?

Para responder a esta pregunta debes tener en cuenta conceptos como la desviación estandar
covarianza, promedio y cuando existe la correlación de Pearson. El valor del índice de correlación
varía en el intervalo [-1,1], indicando el signo el sentido de la relación:

    Si r = 1, existe una correlación positiva perfecta.
    Si 0 < r < 1, existe una correlación positiva.
    Si r = 0, no existe relación lineal.
    Si -1 < r < 0, existe una correlación negativa.
    Si r = -1, existe una correlación negativa perfecta.

Entonces creamos una Lista que contiene el numero de seguidores y de amigos mediante la funcion collect
que recibe nuestra funcion parcial que hace uso de nuestro obejo Tweet y asi se accede a sus atributos
 */
val list= values.collect( { case tweet => (tweet.userFollowersCount,tweet.userFriendsCount) })
/*
en el siguiente bloque de sentencias declaramos la funcion denominada calPearson la cual recibe un parametro
del tipo de dato Lista que contiene tuplas de Double's y que tambien es nuestro resultado esperado, calculamos
la media para ambas variables , hacemos uso de nuestra paremetro recibido  tabla donde mapeamos para sumar todos
sus elementos tabla. map(a=> a._1).sum y luego dividir por su longuitud tabla.length, luego expresamos el caluclo
de la covarianza que es el producto termiano atermino ejecutado,sumado  y del valor obtenido segmentado por la
longuitud de la tabla finalmente disminuido por la diferencia del producto de la media de x e y.
tambn nesecitamos calcular la desviación estándar para X y para Y para hacemos uso del map para elevar al cuadrado
y sumarlos, para asi dividirlos con el numero de elementos, para luego relizar la resta con su media al cuadrado
Finalmente calculamos la correlacion de Pearson que no es mas que la division entre la covarianza y el producto
de las desviaciones estandar de cada variable
 */
def calPearson (tabla: List[(Double, Double)] ): Double ={
  val mediaX= (tabla. map(a=> a._1).sum.toDouble/tabla.length)
  val mediaY= (tabla. map(a=> a._2).sum.toDouble/tabla.length)
  val covarianza = ((tabla. map(a=> a._1 *a._2).sum )/ tabla.length) -(mediaX *mediaY)
  val desX= Math.sqrt((tabla. map(x=> Math.pow(x._1,2)).sum/ tabla.length)- Math.pow(mediaX,2))
  val desY =Math.sqrt((tabla. map(y=> Math.pow(y._2,2)).sum/ tabla.length)- Math.pow(mediaY,2))
  covarianza/(desX*desY)
}
//Invocamos a nuestra funcion y pasamos la lista con el # de amigos y seguidores
calPearson(list)
// presentamos el resultado
val resultado= if(calPearson(list) <1 && calPearson(list) >0) {println("Si exite relación entre seguidores y amigos")}
else {println("No exite relación entre seguidores y amigos")}

case class UserTweet( nombreUs: String,texto: String,isRt:Boolean)
//Creacion del objeto UserTweet con atributos nombreUs y isRt
println(values.length)
// Funcion valida si es twewt o no
def esTweet(text: String):Boolean={ text.startsWith("RT") || text.startsWith("Retweet")}

// valor que llama a cada uno de los datos de text
val textClassify = values.map(tweet => UserTweet(tweet.fromUser, tweet.text,esTweet(tweet.text) ))
  .groupBy(userT => (userT.nombreUs,userT.isRt)).map({case (k,v) => (k, v.length)})

def processFFCounters(ffData:List[Tuple3[String,Int,Int]], isFollowers:Boolean) : Int = {
  def avg =(nums: List[Int])=> nums.sum / nums.length
  val countersList = ffData.map(t3=>(t3._2,t3._3))
  ///isFollowers es el parametro que nosostros le damos según lo que nesecitemos
  if(isFollowers)
    avg(countersList.flatMap(t2 => List(t2._1)))
  else
    avg(countersList.flatMap(t2 => List(t2._2)))
}
// valor con tuplas que llaman a las funciones y devuelven (usuario, seguidores, seguidos, tw,Rtw)
values.map(tweet => (tweet.fromUser,tweet.userFollowersCount.toInt, tweet.userFriendsCount.toInt) ).groupBy(_._1).
  map(kv => (kv._1,
    processFFCounters(kv._2,true),
    processFFCounters( kv._2,false),
    textClassify.get((kv._1,false)).getOrElse(0),
    textClassify.get((kv._1,true)).getOrElse(0)
  ))
