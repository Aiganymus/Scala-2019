package lab10.Serializers

import lab10.model.TelegramMessage
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait TelegramSerializer extends DefaultJsonProtocol with SprayJsonSerializer {
  implicit val messageFormat: RootJsonFormat[TelegramMessage] = jsonFormat2(TelegramMessage)
}
