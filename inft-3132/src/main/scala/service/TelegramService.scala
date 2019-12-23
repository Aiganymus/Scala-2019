package service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, RequestEntity}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import response.TelegramMessage
import serializers.TelegramSerializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

case class TelegramService(message: TelegramMessage) extends TelegramSerializer {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config: Config = ConfigFactory.load()
  val token: String = "999495885:AAG1e6HyhEowf3Fh8sflvBP_dGQiDFy-Jl0"

  val httpRequest: Future[HttpResponse] = Marshal(message).to[RequestEntity].flatMap { entity =>
    val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot$token/sendMessage", Nil, entity)
    println("Request: {}", request)
    Http().singleRequest(request)
  }

  httpRequest.onComplete {
    case Success(value) =>
      println(s"Response: $value")
      value.discardEntityBytes()

    case Failure(exception) =>
      println(s"Error: ${exception.getMessage}")
  }

}
