package lab10

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{delete, put, _}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import lab10.Serializers.SprayJsonSerializer
import lab10.actor.AnimeManager
import lab10.model.{Anime, ErrorResponse, SuccessfulResponse}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

object Boot extends App with SprayJsonSerializer {

  implicit val system: ActorSystem = ActorSystem("anime-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)

  val animeManager = system.actorOf(AnimeManager.props(), "anime-manager")

  val route =
    pathPrefix("anime-list") {
      path("anime" / Segment) { animeId =>
        concat(
          get {
            complete {
              (animeManager ? AnimeManager.ReadAnime(animeId)).mapTo[Either[ErrorResponse, Anime]]
            }
          },
          delete {
            complete {
              (animeManager ? AnimeManager.DeleteAnime(animeId)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
            }
          }
        )
      } ~
        path("anime") {
          concat(
            post {
              entity(as[Anime]) { anime =>
                complete {
                  (animeManager ? AnimeManager.CreateAnime(anime)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
                }
              }
            },
            put {
              entity(as[Anime]) { anime =>
                complete {
                  (animeManager ? AnimeManager.UpdateAnime(anime)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
                }
              }
            }
          )
        }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
}