package memeProject

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import memeProject.model.{Author, ErrorResponse, Meme, SuccessfulResponse}
import memeProject.services.MemeService
import akka.util.Timeout
import akka.pattern.ask
import memeProject.routers.Router
import memeProject.serializers.ElasticSerializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object Boot extends App with ElasticSerializer {
  implicit val system: ActorSystem = ActorSystem("meme-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val client = ElasticSearchClient.client

  // ElasticSearchClient.createEsIndex("memes")

  val route = Router().route

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
}
