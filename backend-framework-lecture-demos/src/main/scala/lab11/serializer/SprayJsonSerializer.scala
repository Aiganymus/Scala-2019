package lab11.serializer

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import lab11.model.{ErrorResponse, Path, SuccessfulResponse}

trait SprayJsonSerializer extends DefaultJsonProtocol {
  implicit val successfulFormat: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)

  implicit val pathFormat: RootJsonFormat[Path] = jsonFormat1(Path)
}
