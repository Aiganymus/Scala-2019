package memeProject.serializers

import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import memeProject.model.Meme
import spray.json._
import scala.util.Try

trait ElasticSerializer extends SprayJsonSerializer {

  // object -> JSON string
  implicit object MemeIndexable extends Indexable[Meme] {
    override def json(meme: Meme): String = meme.toJson.compactPrint
  }

  // JSON string -> object
  // parseJson is a spray method
  implicit object memeHitReader extends HitReader[Meme] {
    override def read(hit: Hit): Either[Throwable, Meme] = {
      Try {
        val jsonAst = hit.sourceAsString.parseJson
        jsonAst.convertTo[Meme]
      }.toEither
    }
  }
}