package models

sealed trait Response

case class SuccessfulResponse(status: Int, message: String) extends Response
case class PhotoResponse(status: Int, message: Array[Byte], contentType: String) extends Response
case class ErrorResponse(status: Int, message: String) extends Response