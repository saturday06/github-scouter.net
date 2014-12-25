package controllers

import com.google.common.base.Charsets
import models._
import org.joda.time.DateTime
import play.Play
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.json.Json._
import scala.sys.process.{ProcessIO, ProcessLogger, Process}
import scala.sys.process.Process._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object PowerLevelsController extends Controller {
  implicit val powerLevelFormat = Json.format[PowerLevel]

  def show(userName: String) = DBAction { implicit rs =>
    // これらの処理はフラットに縦にならべられないか
    val query = TableQuery[PowerLevelsTable]
      .filter(_.userName === userName) // TODO: 大文字小文字マッチ必要かも？
      .sortBy(_.timestamp.desc)
      .sortBy(_.id.desc) // 上とくっつけらんないか？
      .firstOption
    val response = query match {
      case Some(model) => Ok(toJson(model))
      case _ => {
        PowerLevels.executeScouter(userName) match {
          case Some(model) => {
            TableQuery[PowerLevelsTable].insert(model) // TODO: テーブルロック待ったなし
            Ok(toJson(model))
          }
          case _ => NotFound
        }
      }
    }
    response.withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
  }
}
