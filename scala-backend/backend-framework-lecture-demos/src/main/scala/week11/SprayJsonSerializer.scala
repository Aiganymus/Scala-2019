package week11

import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import week11.models.{ErrorResponse, PathModel, SuccessfulResponse}


trait SprayJsonSerializer extends DefaultJsonProtocol {
  implicit val successfulFormat: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)

  implicit val pathFormat: RootJsonFormat[PathModel] = jsonFormat1(PathModel)
}
