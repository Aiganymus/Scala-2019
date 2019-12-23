package week6

import akka.actor.ActorSystem
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

  val movieManager = system.actorOf(MovieManager.props(), "movie-manager")


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
            val output: Future[Either[ErrorResponse, Movie]] = (movieManager ? MovieManager.ReadMovie(movieId)).mapTo[Either[ErrorResponse, Movie]]
            onSuccess(output) {
              case Left(error) => complete(error.status, error)
              case Right(movie) => complete(200, movie)
            }
          } ~
            delete {
              handle((movieManager ? MovieManager.DeleteMovie(movieId)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
            }
        } ~
          path("movie") {
            post {
              entity(as[Movie]) { movie =>
                handle((movieManager ? MovieManager.CreateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
              }
            } ~
              put {
                entity(as[Movie]) { movie =>
                  handle((movieManager ? MovieManager.UpdateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]])
                }
              }
          }
      }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  def handle(output: Future[Either[ErrorResponse, SuccessfulResponse]]) = {
    onSuccess(output) {
      case Left(error) => complete(error.status, error)
      case Right(successful) => complete(successful.status, successful)
    }
  }


  //  val testBot = system.actorOf(TestBot.props(movieManager), "test-bot")


  //  // test create
  //  testBot ! TestBot.TestCreate
  //
  //  // test update
  //  testBot ! TestBot.TestUpdate
  //
  //  testBot ! TestBot.TestRead

  // test conflict
  // testBot ! TestBot.TestConflict
  // testBot ! "bla-bla"

  // test delete
  // testBot ! TestBot.TestDelete
  // testBot ! TestBot.TestDelete

  // test read
  // testBot ! TestBot.TestRead

  // test read not found
  // testBot ! TestBot.TestNotFound

  //  val route =
  //    path("healthcheck" /) {
  //      get {
  //        complete {
  //          "OK"
  //        }
  //      }
  //    } ~
  //      pathPrefix("kbtu-cinema") {
  //        path("movie" / Segment) { movieId =>
  //          get {
  //            val output: Future[Either[ErrorResponse, Movie]] = (movieManager ? MovieManager.ReadMovie(movieId)).mapTo[Either[ErrorResponse, Movie]]
  //            onSuccess(output) {
  //              case Left(err) => complete(err.status, err)
  //              case Right(mov) => complete(200, mov)
  //            }
  //          } ~
  //            delete {
  //              complete {
  //                (movieManager ? MovieManager.DeleteMovie(movieId)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
  //              }
  //            }
  //        } ~
  //          path("movie") {
  //            post {
  //              entity(as[Movie]) { movie =>
  //                complete {
  //                  (movieManager ? MovieManager.CreateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
  //                }
  //              }
  //            } ~
  //              put {
  //                entity(as[Movie]) { movie =>
  //                  complete {
  //                    (movieManager ? MovieManager.UpdateMovie(movie)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
  //                  }
  //                }
  //              }
  //          }
  //      }


}