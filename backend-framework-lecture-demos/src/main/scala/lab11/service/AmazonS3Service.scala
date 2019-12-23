package lab11.service

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

object AmazonS3Service {
  val config: Config = ConfigFactory.load()
  private val accessKeyId =  config.getString("amazon.access-key-id")
  private val secretAccessKey = config.getString("amazon.secret-access-key")
  private val clientRegion = Regions.EU_NORTH_1
  private val credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey)
  private val log = LoggerFactory.getLogger(this.getClass)

  val bucketName1 = "scala-task-1"
  val bucketName2 = "scala-task-2"
  val pathS3 = "./src/main/resources/s3"
  val pathIn = "./src/main/resources/in"
  val pathOut = "./src/main/resources/out"

  val client: AmazonS3 = AmazonS3ClientBuilder.standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(clientRegion)
    .build()

  def createBucket(bucketName: String): Unit = {
    if (!client.doesBucketExistV2(bucketName)) {
      client.createBucket(bucketName)
      log.info(s"Bucket with name: $bucketName created")
    } else {
      log.info(s"Bucket $bucketName already exists")
    }
  }
}
