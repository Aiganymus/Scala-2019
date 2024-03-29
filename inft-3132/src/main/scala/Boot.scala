import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern.ask
import akka.stream.javadsl.StreamConverters
import akka.util.Timeout
import response.Response
import serializers.JsonSupport
import service.PhotoService

import scala.concurrent.duration._
//import java.time.Duration

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.{CreateBucketRequest, GetBucketLocationRequest}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn


object Boot extends App with JsonSupport {
  implicit val system: ActorSystem             = ActorSystem("final-exam-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // needed fot akka's ASK pattern
  implicit val timeout: Timeout = Timeout(60.seconds)

  val bucketName = "final-user-bucket-aiganym-1234"

  // amazon credentials
  val awsCreds = new BasicAWSCredentials("AKIAXNXIW2MPIRH4WSI7", "O91HVwuk1nX+gB8XNB4Q4eLiUxlep1wB5jeiDR+l")

  private val clientRegion = Regions.EU_NORTH_1

  val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard()
    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
    .withRegion(clientRegion)
    .build()

  if (!s3Client.doesBucketExistV2(bucketName)) {
    s3Client.createBucket(bucketName)
  } else {
    println(s"Bucket $bucketName already exists")
  }

  val photoService: ActorRef = system.actorOf(PhotoService.props(s3Client, bucketName))

  val route: Route = {
    pathPrefix("photo" / Segment) { userId =>
      concat(
        post {

          // example: POST localhost:8081/photo/user-12
          fileUpload("photoUpload") {
            case (fileInfo, fileStream) =>
              // Photo upload
              // fileInfo -- information about file, including FILENAME
              // filestream -- stream data of file

              val inputStream = fileStream.runWith(StreamConverters.asInputStream(5.seconds))
              complete {
                (photoService ? PhotoService.UploadPhoto(inputStream, userId, fileInfo.fileName, fileInfo.contentType.toString())).mapTo[Either[Response.Error, Response.Accepted]]
              }
          }
        },
        path(Segment) { fileName =>
          concat(
            get {
              println(fileName)
              complete {
                (photoService ? PhotoService.GetPhoto(userId, fileName)).mapTo[Either[Response.PhotoUrl, Response.Error]]
              }
            },
            delete {
              complete {
                (photoService ? PhotoService.DeletePhoto(userId, fileName)).mapTo[Either[Response.PhotoUrl, Response.Error]]
              }
            }
          )
          // TODO: implement GET method
          // example: GET localhost:8081/photo/user-12/2.png

          // TODO: implement DELETE method
          // example: DELETE localhost:8081/photo/user-12/6.png
        }
      )
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)
  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done
}