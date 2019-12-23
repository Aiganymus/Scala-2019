package week6.actor

import akka.actor.{Actor, ActorLogging, Props}
import week6.model.{ErrorResponse, Movie, SuccessfulResponse}

// props
// messages
object MovieManager {

  // Create
  case class CreateMovie(movie: Movie)

  // Read
  case class ReadMovie(id: String)

  // Update
  case class UpdateMovie(movie: Movie)

  // Delete
  case class DeleteMovie(id: String)

  def props() = Props(new MovieManager)
}

// know about existing movies
// can create a movie
// can manage movie
class MovieManager extends Actor with ActorLogging {

  // import companion OBJECT
  import MovieManager._

  var movies: Map[String, Movie] = Map()

  override def receive: Receive = {

    case CreateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(_) =>
          log.warning(s"Could not create a movie with ID: ${movie.id} because it already exists.")
          sender() ! Left(ErrorResponse(409, s"Movie with ID: ${movie.id} already exists."))

        case None =>
          movies = movies + (movie.id -> movie)
          log.info("Movie with ID: {} created.", movie.id)
          sender() ! Right(SuccessfulResponse(201, s"Movie with ID: ${movie.id} created."))
      }

    case msg: ReadMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          log.info("Movie with ID: {} was returned.", movie.id)
          sender() ! Right(movie)

        case None =>
          log.warning("Movie with ID: {} not found.", msg.id)
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }

    case UpdateMovie(updatedMovie) =>
      movies.get(updatedMovie.id) match {
        case Some(movie) =>
          movies = movies + (movie.id -> updatedMovie)
          log.info("Movie with ID: {} updated.", movie.id)
          sender() ! Right(SuccessfulResponse(200, s"Movie with ID: ${movie.id} was updated."))
        case None =>
          log.warning("Movie with ID: {} not found.", updatedMovie.id)
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${updatedMovie.id} not found."))
      }

    case msg: DeleteMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          movies = movies - movie.id
          log.info("Movie with ID: {} deleted.", movie.id)
          sender() ! Right(SuccessfulResponse(204, s"Movie with ID: ${movie.id} was deleted."))
        case None =>
          log.warning("Movie with ID: {} not found.", msg.id)
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }
  }
}
