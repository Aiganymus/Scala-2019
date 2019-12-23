package lab10.Serializers

import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import spray.json._
import scala.util.Try
import lab10.model.Anime

trait ElasticSerializer extends SprayJsonSerializer  {

  implicit object AnimeIndexable extends Indexable[Anime] {
    override def json(anime: Anime): String = anime.toJson.compactPrint
  }

  implicit object AnimeHitReader extends HitReader[Anime] {
    override def read(hit: Hit): Either[Throwable, Anime] = {
      Try {
        val json = hit.sourceAsString.parseJson
        json.convertTo[Anime]
      }.toEither
    }
  }
}
