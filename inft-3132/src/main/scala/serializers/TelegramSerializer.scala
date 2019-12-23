package serializers

import response.TelegramMessage
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait TelegramSerializer extends DefaultJsonProtocol with JsonSupport {
  implicit val messageFormat: RootJsonFormat[TelegramMessage] = jsonFormat2(TelegramMessage)
}
