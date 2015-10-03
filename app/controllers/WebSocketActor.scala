package controllers

import akka.actor._
import Implicits._
import com.example.tagrank.api._
import play.api.libs.json.{JsString, JsValue, Json}


object WebSocketActor {

  def props(out: ActorRef) = Props(new WebSocketActor(out))

}

class WebSocketActor(out: ActorRef) extends Actor with ActorLogging {

  lazy val rankingAnalyzer =
    context.actorSelection("akka.tcp://backend@127.0.0.1:2552/user/rankingAnalyzer")

  def receive = {

    case msg: JsValue =>
      rankingAnalyzer ! AnalyzeRanking

    case msg: Array[Ranking] =>
      out ! Json.toJson(msg)
      
  }

}