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
          log.info("Movie with ID: {} found [READ].", movie.id)
          sender() ! Right(movie);

        case None =>
          log.info("Movie with ID: {} not found [READ].", msg.id);
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found [READ]."))
      }

    case msg: DeleteMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          movies -= movie.id;
          log.info("Movie with ID: {} deleted.", movie.id);
          sender() ! Right(SuccessfulResponse(200, s"Movie with ID: ${movie.id} deleted."))

        case None =>
          log.info("Movie with ID: {} not found [DELETE].", msg.id);
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found [DELETE]."))
      }

    case UpdateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(_) => // Some(existingMovie) -> no reason
          movies = movies + (movie.id -> movie)
          log.info("Movie with ID: {} updated.", movie.id)
          sender() ! Right(SuccessfulResponse(200, s"Movie with ID: ${movie.id} updated."))

        case None =>
          log.warning(s"Could not update a movie with ID: ${movie.id} because such ID does not exist.")
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${movie.id} does not exist."))
      }
  }

  def randomInt() =
  // FIXME: use random
    4
}
