package lab10.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import com.sksamuel.elastic4s.{ElasticsearchClientUri, IndexAndType}
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure}
import lab10.model.{Anime, ErrorResponse, SuccessfulResponse, TelegramMessage}
import lab10.Serializers.ElasticSerializer
import lab10.service.TelegramService
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object AnimeManager {

  // Create
  case class CreateAnime(anime: Anime)

  // Read
  case class ReadAnime(id: String)

  // Update
  case class UpdateAnime(anime: Anime)

  // Delete
  case class DeleteAnime(id: String)

  def props() = Props(new AnimeManager)

}

class AnimeManager extends Actor with ActorLogging with ElasticSerializer {
  import AnimeManager._

  implicit val system: ActorSystem = ActorSystem("telegram-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val client: HttpClient = HttpClient(ElasticsearchClientUri("localhost", 9200))
  val indexType: IndexAndType = "animes" / "_doc"
  val chat_id: Int = -352088280

  override def receive: Receive = {
    case CreateAnime(anime: Anime)  =>
      val cmd = client.execute(indexInto(indexType).id(anime.id).doc(anime))
      val replyTo = sender()

      cmd.onComplete {
        case Success(either) => either match {
            case Right(_) =>
              replyTo ! Right(201, s"Anime with ID = ${anime.id} created.")
              sendTelegramMsg(201, s"Anime with ID = ${anime.id} successfully created.")
            case Left(failure: RequestFailure) =>
              replyTo ! Left(failure.status, s"Internal server error.\n${failure.body}")
          }

        case Failure(failure: Throwable) =>
          replyTo ! Left(503, s"Internal server error.\n${failure.getMessage}")
      }

    case ReadAnime(id) =>
      val cmd = client.execute(get(id).from(indexType))
      val replyTo = sender()

      cmd.onComplete {
        case Success(either) => either match {
          case Right(right) =>
            if (right.result.found) {
              either.map(e => e.result.to[Anime]).foreach { anime =>
                replyTo ! Right(anime)
                sendTelegramMsg(200, s"Anime with ID = ${id} found.")
              }
            } else {
              replyTo ! Left(404, s"Anime with id = ${id} not found.")
            }

          case Left(failure: RequestFailure) =>
            replyTo ! Left(failure.status, s"Internal server error.\n${failure.body}")
        }

        case Failure(exception: Throwable) =>
          replyTo ! Left(503, s"Internal server error.\n${exception.getMessage}")
      }

    case UpdateAnime(anime) =>
      val cmd = client.execute(update(anime.id).in(indexType).docAsUpsert(anime))
      val replyTo = sender()

      cmd.onComplete {
        case Success(either) => either match {
          case Right(_) =>
            replyTo ! Right(200, s"Anime with ID = ${anime.id} updated.")
            sendTelegramMsg(200, s"Anime with ID = ${anime.id} updated.")
          case Left(failure: RequestFailure) =>
            replyTo ! Left(failure.status, s"Internal server error.\n${failure.body}")
        }

        case Failure(exception: Throwable) =>
          replyTo ! Left(503, s"Internal server error.\n${exception.getMessage}")
      }

    case DeleteAnime(id) =>
      val cmd = client.execute(delete(id).from(indexType))
      val replyTo = sender()

      cmd.onComplete {
        case Success(either) => either match {
          case Right(right) =>
            if (right.result.result == "deleted") {
              replyTo ! Right(204, s"Anime with ID = ${id} deleted.")
              sendTelegramMsg(204, s"Anime with ID = ${id} deleted.")
            } else {
              replyTo ! Left(404, s"Anime with id = ${id} not found.")
            }
          case Left(failure: RequestFailure) =>
            replyTo ! Left(failure.status, s"Internal server error.\n${failure.body}")
        }

        case Failure(exception: Throwable) =>
          replyTo ! Left(503, s"Internal server error.\n${exception.getMessage}")
      }
  }

  private def sendTelegramMsg(status: Int, text: String): Unit = {
    val msg = TelegramMessage(chat_id, s"status: ${status}\nmsg: $text")
    TelegramService(msg)
  }

}