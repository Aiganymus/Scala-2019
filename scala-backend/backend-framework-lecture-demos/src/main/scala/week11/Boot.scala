package week11

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.slf4j.LoggerFactory
import week11.actors.FileManagerActor
import week11.actors.FileManagerActor.{DownloadAllFiles, GetFile, UploadAllFiles, UploadFile}
import week11.models.{ErrorResponse, PathModel, SuccessfulResponse}

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration._


object Boot extends App with SprayJsonSerializer {
  implicit val system: ActorSystem = ActorSystem("file-manager-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val timeout: Timeout = Timeout(10.seconds)

  val log = LoggerFactory.getLogger(this.getClass)

  val clientRegion: Regions = Regions.EU_CENTRAL_1

  val credentials = new BasicAWSCredentials("access-key", "secret-key")

  val client: AmazonS3 = AmazonS3ClientBuilder.standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(clientRegion)
    .build()

  val bucketName = "test-bucket775745"

  val worker = system.actorOf(FileManagerActor.props(client, bucketName))

  createBucket(client, bucketName)

  val route =
    concat(
      path("file") {
        concat(
          get {
            parameters('filename.as[String]) { fileName =>
              handle((worker ? GetFile(fileName)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
            }
          },
          post {
            entity(as[PathModel]) { pathModel =>
              handle((worker ? UploadFile(pathModel.path)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
            }
          }
        )
      },
      pathPrefix("task2") {
        concat(
          path("in") {
            get {
              complete {
                worker ! DownloadAllFiles
                "done_in"
              }
            }
          },
          path("out") {
            get {
              complete {
                worker ! UploadAllFiles
                "done_out"
              }
            }
          }
        )
      }
    )


  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)


  def createBucket(s3client: AmazonS3, bucket: String): Unit = {
    if (!s3client.doesBucketExistV2(bucket)) {
      s3client.createBucket(bucket)
      log.info(s"Bucket with name: $bucket created")
    } else {
      log.info(s"Bucket $bucket already exists")
    }
  }

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