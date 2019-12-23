package memeProject.services


import memeProject.model.{Author, ErrorResponse, Meme, SuccessfulResponse}
import akka.actor.{Actor, ActorLogging, Props}
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import memeProject.serializers.ElasticSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.pattern.pipe

object MemeService {

  val index = "memes"
  val mappingType = "_doc"

  case class PostMeme(meme: Meme)

  case class GetMeme(id: String)

  case class PutMeme(meme: Meme)

  case class DeleteMeme(id: String)

  def props(esClient: HttpClient) = Props(new MemeService(esClient))

}

class MemeService(esClient: HttpClient) extends Actor with ActorLogging with ElasticSerializer {

  import MemeService._

  override def receive: Receive = {
    case PostMeme(meme) =>
      val replyTo = sender()
      val cmd = esClient.execute(indexInto(index / mappingType).id(s"${meme.id}").doc(meme))

      cmd.onComplete {
        case Success(_) =>
          log.info("Meme with ID: {} created.", meme.id)
          replyTo ! Right(SuccessfulResponse(201, s"Meme with ID: ${meme.id} created."))

        case Failure(_) =>
          log.warning(s"Could not create a meme with ID: ${meme.id}. Internal Server Error.")
          replyTo ! Left(ErrorResponse(500, s"Could not create a meme with ID: ${meme.id}. Internal Server Error."))
      }

    case msg: GetMeme =>
      val replyTo = sender()
      val cmd = esClient.execute {
        get(msg.id).from(index / mappingType)
      }

      cmd.onComplete {
        case Success(either) =>
          either.map(e => e.result.safeTo[Meme]).foreach { meme => {
            meme match {
              case Left(_) =>
                log.info("Meme with ID: {} not found [GET].", msg.id);
                replyTo ! Left(ErrorResponse(404, s"Meme with ID: ${msg.id} not found [GET]."))
              case Right(meme) =>
                log.info("Meme with ID: {} found [GET].", meme.id)
                replyTo ! Right(meme)
            }
          }
          }
        case Failure(fail) =>
          log.warning(s"Could not read a meme with ID: ${msg.id}. Exception with MESSAGE: ${fail.getMessage} occurred during this request. [GET]")
          replyTo ! Left(ErrorResponse(500, fail.getMessage))
      }

    case PutMeme(meme) =>
      val replyTo = sender()
      val cmd = esClient.execute {
        update(meme.id).in(index / mappingType).doc(meme)
      }

      cmd.onComplete {
        case Success(either) => either match {
          case Left(_) =>
            log.warning("Meme with ID: {} not found [UPDATE].", meme.id)
            replyTo ! Left(ErrorResponse(404, s"Meme with ID: ${meme.id} not found [UPDATE]."))
          case Right(_) =>
            log.info("Meme with ID: {} updated.", meme.id)
            replyTo ! Right(SuccessfulResponse(200, s"Meme with ID: ${meme.id} updated."))
        }
        case Failure(_) =>
          log.warning(s"Could not update a meme with ID: ${meme.id}. Internal Server Error.")
          replyTo ! Left(ErrorResponse(500, s"Could not update a meme with ID: ${meme.id}. Internal Server Error."))
      }

    case msg: DeleteMeme =>
      val replyTo = sender()
      val cmd = esClient.execute {
        delete(msg.id).from(index / mappingType)
      }

      cmd.onComplete {
        case Success(either) =>
          either.map(e => e.result.result.toString).foreach { res => {
            res match {
              case "deleted" =>
                log.info("Meme with ID: {} deleted.", msg.id);
                replyTo ! Right(SuccessfulResponse(200, s"Meme with ID: ${msg.id} deleted."))
              case "not_found" =>
                log.info("Meme with ID: {} not found [DELETE].", msg.id);
                replyTo ! Left(ErrorResponse(404, s"Meme with ID: ${msg.id} not found [DELETE]."))
            }
          }
          }
        case Failure(fail) =>
          log.warning(s"Could not delete a meme with ID: ${msg.id}. Exception with MESSAGE: ${fail.getMessage} occurred during this request. [DELETE]")
          replyTo ! Left(ErrorResponse(500, fail.getMessage))
      }
  }
}