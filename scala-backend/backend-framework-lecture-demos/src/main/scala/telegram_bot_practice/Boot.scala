package telegram_bot_practice

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Boot extends App with Serializer {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config: Config = ConfigFactory.load() // config
  val log = LoggerFactory.getLogger("Boot")

  //  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))

  val token = "995579635:AAFVa2cuVPxBVq47tBe9WdXRBcSnp5kEMCE" // config.getString("telegram.token") // token
  log.info(s"Token: $token")


  val message: TelegramMessage = TelegramMessage(-371266564, "ISLAM NORMALNO OBWAYSYA")

  val httpReq = Marshal(message).to[RequestEntity].flatMap { entity =>
    val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot$token/sendMessage", Nil, entity)
    log.debug("Request: {}", request)
    Http().singleRequest(request)
  }

  httpReq.onComplete {
    case Success(value) =>
      log.info(s"Response: $value")
      value.discardEntityBytes()

    case Failure(exception) =>
      log.error("error")
  }

  Thread.sleep(5000)


}
