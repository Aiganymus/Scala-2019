package memeProject.serializers

import com.sksamuel.elastic4s.http.RequestSuccess
import memeProject.model.{Author, ErrorResponse, Meme, SuccessfulResponse, TelegramMessage}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait SprayJsonSerializer extends DefaultJsonProtocol {
  implicit val authorFormat: RootJsonFormat[Author] = jsonFormat5(Author)
  implicit val memeFormat: RootJsonFormat[Meme] = jsonFormat5(Meme)

  implicit val successfulResponse: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)

  implicit val messageFormat: RootJsonFormat[TelegramMessage] = jsonFormat2(TelegramMessage)

}
