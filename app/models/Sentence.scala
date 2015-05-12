package models

import play.api.libs.json.Json

case class Sentence(content: String)

object Sentence {
    implicit val sentenceFormat = Json.format[Sentence]
}