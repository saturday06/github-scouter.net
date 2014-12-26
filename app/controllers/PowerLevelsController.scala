package controllers

import models._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.json.Json._

object PowerLevelsController extends Controller {
  implicit val powerLevelFormat = Json.format[PowerLevel]

  def show(userName: String) = Action { implicit rs =>
    val response = PowerLevels.findOrCreateByUserName(userName) match {
      case Some(powerLevel) => Ok(toJson(powerLevel))
      case None => NotFound
    }
    response.withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*") // TODO: もっと共通の場所に書く
  }
}
