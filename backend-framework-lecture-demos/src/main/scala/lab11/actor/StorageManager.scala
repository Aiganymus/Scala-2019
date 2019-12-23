package lab11.actor
import java.io.File
import java.nio.file.Paths

import akka.actor.{Actor, ActorLogging, Props}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.{GetObjectRequest, ListObjectsRequest, ObjectMetadata, PutObjectRequest, PutObjectResult}
import lab11.model.{ErrorResponse, SuccessfulResponse}
import lab11.service.AmazonS3Service

import scala.util.{Failure, Success, Try}

object StorageManager {

  case class GetFile(fileName: String)

  case class UploadFile(fileName: String)

  case object UploadAllFiles

  case object DownloadAllFiles

  def props(client: AmazonS3, bucketName: String) = Props(new StorageManager(client, bucketName))

}

class StorageManager(client: AmazonS3, bucketName: String) extends Actor with ActorLogging {

  import StorageManager._

  def downloadFile(client: AmazonS3, bucketName: String, objectKey: String, fullPath: String): ObjectMetadata = {
    val file: File = new File(fullPath)
    val dirs: File = file.getParentFile
    if (dirs != null) {
      dirs.mkdirs()
    }
    client.getObject(new GetObjectRequest(bucketName, objectKey), file)
  }

  def uploadFile(client: AmazonS3, bucketName: String, objectKey: String, filePath: String): PutObjectResult = {
    val metadata = new ObjectMetadata()
    metadata.setContentType("plain/text")
    metadata.addUserMetadata("user-type", "customer")

    val request = new PutObjectRequest(bucketName, objectKey, new File(filePath))
    request.setMetadata(metadata)
    client.putObject(request)
  }

  override def receive: Receive = {
    case GetFile(fileName: String) =>
      val replyTo = sender()

      if (client.doesObjectExist(bucketName, fileName)) {
        val fullPath = s"${AmazonS3Service.pathS3}/$fileName"
        downloadFile(client, bucketName, fileName, fullPath)
        replyTo ! Right(SuccessfulResponse(200, s"'$fileName' was downloaded."))
        log.info(s"Downloaded file-object '$fileName' from AWS S3")
      } else {
        replyTo ! Left(ErrorResponse(404, s"'$fileName' was not found."))
        log.info(s"Failed to download file-object '$fileName'. '$fileName' isn't in the bucket '$bucketName'.")
      }

    case UploadFile(fileName) =>
      val replyTo = sender()

      if (client.doesObjectExist(bucketName, fileName)) {
        replyTo ! Left(ErrorResponse(409, s"${fileName} already exists."))
        log.info(s"Failed to upload file-object '$fileName'. '$fileName' is already in the bucker '$bucketName'.")
      } else {
        val filePath = s"${AmazonS3Service.pathS3}/$fileName"

        Try(uploadFile(client, bucketName, fileName, filePath)) match {
          case Success(_) =>
            replyTo ! Right(SuccessfulResponse(201, s"'${fileName}' was uploaded."))
            log.info(s"Uploaded file-object '$fileName' into '$bucketName'.")
          case Failure(exception) =>
            replyTo ! Left(ErrorResponse(503, s"Internal server error: ${exception.getMessage}."))
            log.info(s"Failed to upload file-object '$fileName'. ${exception.getMessage}.")
        }
      }

    case UploadAllFiles =>
      val mainDirectory: File = new File(AmazonS3Service.pathOut)
      uploadDirectoryContents(mainDirectory)

      def uploadDirectoryContents(dir: File): Unit = {
        val files: Array[File] = dir.listFiles()

        for (file <- files) {
          var path = Paths.get(file.getPath)
          println(path)
          path = path.subpath(5, path.getNameCount)

          if (file.isDirectory)
            uploadDirectoryContents(file)
          else
            uploadFile(client, bucketName, path.toString, s"${AmazonS3Service.pathOut}/${path.toString}")
        }
      }

    case DownloadAllFiles =>
      val objects = client.listObjects(new ListObjectsRequest().withBucketName(bucketName))
      objects.getObjectSummaries.forEach(objectSummary => downloadFile(client, bucketName, objectSummary.getKey, s"${AmazonS3Service.pathIn}/${objectSummary.getKey}"))
  }
}
