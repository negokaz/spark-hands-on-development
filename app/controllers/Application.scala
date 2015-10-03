package controllers

import javax.inject.Inject

import akka.actor._
import play.api._
import play.api.libs.json.JsValue
import play.api.mvc._

import play.api.Play.current

class Application @Inject()(actorSystem: ActorSystem) extends Controller {

  def index = Action {

    Ok(views.html.main())
  }

  def socket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>

    WebSocketActor.props(out)
  }

}



