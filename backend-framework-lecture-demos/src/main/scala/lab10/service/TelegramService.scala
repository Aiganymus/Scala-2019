package lab10.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import lab10.Serializers.TelegramSerializer
import lab10.model.TelegramMessage
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

case class TelegramService(message: TelegramMessage) extends TelegramSerializer {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val log = LoggerFactory.getLogger("TelegramManager")
  val config: Config = ConfigFactory.load()
  val token = config.getString("telegram.token")

  val httpRequest = Marshal(message).to[RequestEntity].flatMap { entity =>
    val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot$token/sendMessage", Nil, entity)
    log.info("Request: {}", request)
    Http().singleRequest(request)
  }

  httpRequest.onComplete {
    case Success(value) =>
      log.debug(s"Response: $value")
      value.discardEntityBytes()

    case Failure(exception) =>
      log.error(s"Error: ${exception.getMessage}")
  }

}
