package week6

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import week6.actor.{MovieManager, TestBot}
import week6.model.{ErrorResponse, Movie, Response, SuccessfulResponse}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

object Boot extends App with SprayJsonSerializer {

  implicit val system: ActorSystem = ActorSystem("movie-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)

  val movieManager: ActorRef = system.actorOf(MovieManager.props(), "movie-manager")


  val route =
    path("healthcheck") {
      get {
        complete {
          "OK"
        }
      }
    } ~
    pathPrefix("kbtu-cinema") {
      path("movie" / Segment) { movieId =>
        get {
          complete {
            (movieManager ? MovieManager.ReadMovie(movieId)).mapTo[Either[ErrorResponse, Movie]]
          }
        }
      } ~
      path("movie") {
        post {
          entity(as[Movie]) { movie =>
            complete {
              (movieManager ? MovieManager.CreateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
            }
          }
        }
      } ~
      path("movie") {
        put {
          entity(as[Movie]) { movie =>
            complete {
              (movieManager ? MovieManager.UpdateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
            }
          }
        }
      } ~
      path("movie" / Segment) { movieId =>
        delete {
          complete {
            (movieManager ? MovieManager.DeleteMovie(movieId)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)



//  val testBot = system.actorOf(TestBot.props(movieManager), "test-bot")
//  testBot ! TestBot.TestCreate
//  testBot ! TestBot.TestConflict
//  testBot ! "bla-bla"
//  testBot ! TestBot.TestUpdate
//  testBot ! TestBot.TestRead
//  testBot ! TestBot.TestDelete
//  testBot ! TestBot.TestNotFound

}
