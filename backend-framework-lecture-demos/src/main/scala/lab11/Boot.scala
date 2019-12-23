package lab11

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.amazonaws.services.s3.AmazonS3
import lab11.actor.StorageManager
import lab11.actor.StorageManager.{DownloadAllFiles, GetFile, UploadAllFiles, UploadFile}
import lab11.model.{ErrorResponse, Path, SuccessfulResponse}
import lab11.serializer.SprayJsonSerializer
import lab11.service.AmazonS3Service

import scala.concurrent.Future
import scala.concurrent.duration._


object Boot extends App with SprayJsonSerializer {
  implicit val system: ActorSystem = ActorSystem("file-manager-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val timeout: Timeout = Timeout(10.seconds)

  val client: AmazonS3 = AmazonS3Service.client
  AmazonS3Service.createBucket(AmazonS3Service.bucketName1)
  AmazonS3Service.createBucket(AmazonS3Service.bucketName2)
  val storageManager1 = system.actorOf(StorageManager.props(client, AmazonS3Service.bucketName1), "storage-manager-1")
  val storageManager2 = system.actorOf(StorageManager.props(client, AmazonS3Service.bucketName2), "storage-manager-2")

  val route =
    concat(
      pathPrefix("task1") {
        path("file") {
          concat(
            get {
              parameters('filename.as[String]) { fileName =>
                handle((storageManager1 ? GetFile(fileName)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
              }
            },
            post {
              entity(as[Path]) { pathModel =>
                handle((storageManager1 ? UploadFile(pathModel.path)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
              }
            }
          )
        }
      },
      pathPrefix("task2") {
        concat(
          path("in") {
            get {
              complete {
                storageManager2 ! DownloadAllFiles
                "OK"
              }
            }
          },
          path("out") {
            get {
              complete {
                storageManager2 ! UploadAllFiles
                "OK"
              }
            }
          }
        )
      }
    )


  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  def handle(output: Future[Either[ErrorResponse, SuccessfulResponse]]) = {
    onSuccess(output) {
      case Left(error) => {
        complete(error.status, error)
      }
      case Right(successful) => {
        complete(successful.status, successful)
      }
    }
  }
}