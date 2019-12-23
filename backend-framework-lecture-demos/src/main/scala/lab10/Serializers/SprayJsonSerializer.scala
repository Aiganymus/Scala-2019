package lab10.Serializers


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import lab10.model.{Anime, ErrorResponse, Studio, SuccessfulResponse}

trait SprayJsonSerializer extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val successfulResponse: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)

  implicit val studioFormat: RootJsonFormat[Studio] = jsonFormat4(Studio)
  implicit val animeFormat: RootJsonFormat[Anime] = jsonFormat4(Anime)

}
