package memeProject.routers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.{Marshal, ToResponseMarshallable}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import memeProject.model.{Author, ErrorResponse, Meme, SuccessfulResponse, TelegramMessage}
import memeProject.services.MemeService
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import com.sksamuel.elastic4s.http.HttpClient
import com.typesafe.config.{Config, ConfigFactory}
import memeProject.serializers.{ElasticSerializer, SprayJsonSerializer}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

case class Router(implicit val system: ActorSystem, implicit val materializer: Materializer, implicit val client: HttpClient) extends SprayJsonSerializer {
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)

  val memeService = system.actorOf(MemeService.props(client), "meme-service")

  val config: Config = ConfigFactory.load() // config, default - application.conf.
  val log = LoggerFactory.getLogger(this.getClass)

  val token = "here-is-your-token" // config.getString("telegram.token") // token
  log.info(s"Token: $token")

  val chatID = -371266564;

  val route: Route =
    (path("healthcheck") & get) {
      complete {
        "OK"
      }
    } ~
      pathPrefix("kbtu-meme-generator") {
        path("meme" / Segment) { memeId =>
          get {
            val output = (memeService ? MemeService.GetMeme(memeId)).mapTo[Either[ErrorResponse, Meme]]
            onSuccess(output) {
              case Left(error) => {
                sendMessageToBot(s"Status: ${error.status}. Response: ${error.message}")
                complete(error.status, error)
              }
              case Right(meme) => {
                sendMessageToBot(s"Status: 200. Response: ${meme}")
                complete(200, meme)
              }
            }
          } ~
            delete {
              handle((memeService ? MemeService.DeleteMeme(memeId)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
            }
        } ~
          (path("meme")) {
            post {
              entity(as[Meme]) { meme =>
                handle((memeService ? MemeService.PostMeme(meme)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
              }
            } ~
              put {
                entity(as[Meme]) { meme =>
                  handle((memeService ? MemeService.PutMeme(meme)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
                }
              }
          }
      }

  def handle(output: Future[Either[ErrorResponse, SuccessfulResponse]]) = {
    onSuccess(output) {
      case Left(error) => {
        sendMessageToBot(s"Status: ${error.status}. Response: ${error.message}")
        complete(error.status, error)
      }
      case Right(successful) => {
        sendMessageToBot(s"Status: ${successful.status}. Response: ${successful.message}");
        complete(successful.status, successful)
      }
    }
  }

  def sendMessageToBot(msg: String): Unit = {
    val message: TelegramMessage = TelegramMessage(chatID, msg);

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
        log.error(exception.getMessage)
    }
  }
}
