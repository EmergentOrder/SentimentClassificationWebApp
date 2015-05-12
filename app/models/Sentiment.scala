package models

import play.api.libs.json.Json

case class Sentiment(category: String, probability: Double, content: String, uuid: String)

object Sentiment {
    implicit val sentimentFormat = Json.format[Sentiment]
}