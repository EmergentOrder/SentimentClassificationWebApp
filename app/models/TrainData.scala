package models

import play.api.libs.json.Json

case class TrainData(content: String, category: String) 

object TrainData {
    implicit val trainDataFormat = Json.format[TrainData]
}