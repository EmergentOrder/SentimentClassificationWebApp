package controllers

import models.Sentence
import models.TrainData
import models.Sentiment
import models.DB
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.Logger

import io.prediction.EngineClient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import scala.collection.JavaConverters._

import com.google.common.collect.ImmutableMap;
import io.prediction.Event;
import io.prediction.EventClient;

object Application extends Controller {
    
  val engineUrl = "http://localhost:8000"
  val eventUrl = "http://localhost:7070"
  val accessKey = "YOURACCESSKEYHERE"

  val sentenceForm: Form[Sentence] = Form {
    mapping(
        "content" -> text
        ) (Sentence.apply)(Sentence.unapply)  
      
  }
  
   val trainDataForm: Form[TrainData] = Form {
    mapping(
        "content" -> text,
        "category" -> text
        ) (TrainData.apply)(TrainData.unapply)  
      
  }
  
  def getSentenceSentiment = Action { implicit request =>
      val sentence = sentenceForm.bindFromRequest.get
      val engineClient = new EngineClient(engineUrl)

      val map = Map("text"-> sentence.content.asInstanceOf[java.lang.Object]).asJava
      
      //val list = ImmutableList.copyOf[Int](List(2,0,0).asJava).asInstanceOf[java.lang.Object]
      
      //val map = Map("text"-> list).asJava
      
      val response = engineClient.sendQuery(map)
      val sentimentCategory = response.get("category").toString
      val sentimentConfidence = response.get("confidence").toString
      Logger.info(response.toString)
      DB.save(Sentiment(sentimentCategory, sentimentConfidence.toDouble, sentence.content,  request.session.get("uuid").getOrElse("default")))
      Redirect(routes.Application.index())
  }
  
 def trainSentiment = Action { implicit request =>
      val trainData = trainDataForm.bindFromRequest.get 
      val client = new EventClient(accessKey, eventUrl);
      val map = Map( "text" -> trainData.content.asInstanceOf[java.lang.Object],
        "category"-> trainData.category.asInstanceOf[java.lang.Object],
        "label" -> trainData.category.asInstanceOf[java.lang.Object]).asJava

      val userId = request.session.get("uuid")
      // set the 4 properties for a userp
     client.createEvent(new Event()
     .event("$set")
     .entityType("user")
     .entityId(userId.getOrElse("default"))
     .properties(map));
     
      Logger.info("trained".toString)
      Redirect(routes.Application.index())
  }
  
  def getSentiments = Action { request =>
      val sentiments = DB.query[Sentiment].whereEqual("uuid", request.session.get("uuid").getOrElse("default")).fetch
      Ok(Json.toJson(sentiments))
  }
  
    
  def clearHistory = Action { request =>
      //val sentiments = DB.delete[Sentiment].whereEqual("uuid", request.session.get("uuid").getOrElse("default"))
      val sentiments = DB.query[Sentiment].whereEqual("uuid", request.session.get("uuid").getOrElse("default")).fetch
      sentiments foreach DB.delete[Sentiment]
      Redirect(routes.Application.index())
  }
  
  
  
  def index = Action { request =>
  request.session.get("uuid").map { user =>
  Logger.info("uuid is set to "  + request.session.get("uuid"))
     Ok(views.html.index("THIS IS DIFFERENT"))
  }.getOrElse {
       val uuid=java.util.UUID.randomUUID().toString();
       Logger.info("Setting uuid")
       Ok(views.html.index("index")).withSession(request.session + ("uuid" -> uuid))
  }
}

}
