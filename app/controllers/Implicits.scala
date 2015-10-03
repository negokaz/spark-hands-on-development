package controllers

import akka.util.Timeout
import com.example.tagrank.api.Ranking
import play.api.libs.json.Json

import scala.concurrent.duration._

object Implicits {

  implicit val rankingWrites = Json.writes[Ranking]

  implicit val timeout = Timeout(10 seconds)
}
