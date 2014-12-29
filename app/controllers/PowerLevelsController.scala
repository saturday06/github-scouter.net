package controllers

import models._
import play.api.db.slick.DBAction
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc._

object PowerLevelsController extends Controller {
  implicit val powerLevelFormat = Json.format[PowerLevel]

  def show(userName: String) = DBAction { implicit rs =>
    val response = PowerLevels.findOrCreateByUserName(userName)(rs.dbSession) match {
      case Some(powerLevel) => Ok(toJson(powerLevel))
      case None => NotFound
    }
    response.withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*") // TODO: もっと共通の場所に書く
  }
}
