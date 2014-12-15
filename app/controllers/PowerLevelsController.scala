package controllers

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
      .filter(_.userName === userName)
      .sortBy(_.timestamp.desc)
      .sortBy(_.id.desc) // 上とくっつけらんないか？
      .firstOption
    query match {
      case Some(model) => Ok(toJson(model))
      case _ => {
        executeScouter(userName) match {
          case Some(model) => {
            TableQuery[PowerLevelsTable].insert(model) // TODO: テーブルロック待ったなし
            Ok(toJson(model))
          }
          case _ => NotFound
        }
      }
    }
  }

  /**
   * ここに書くんじゃなくて適当なサービスクラスっぽいものを用意
   */
  def executeScouter(userName: String): Option[PowerLevel] = {
    val out = new StringBuilder
    val err = new StringBuilder

    // TODO: Possible OS command injection
    val process = Process(Seq("./node_modules/.bin/github-scouter", "--json", "--no-cache", userName), Play.application.path)
    val exitStatus = process.!(ProcessLogger(
      (o: String) => out.append(o),
      (e: String) => err.append(e)))
    if (exitStatus != 0) {
      return None
    }

    // TODO: モデルに移す。よりエレガントなウェイを使う。
    val json = Json.parse(out.toString())
    val id = 0 // これで良いのか？
    val attack = json.\("attack").as[Int]
    val intelligence = json.\("intelligence").as[Int]
    val agility = json.\("agility").as[Int]
    val timestamp = json.\("timestamp").as[Int]
    val createdAt = DateTime.now()
    val updatedAt = createdAt
    Some(PowerLevel(id, userName, attack, intelligence, agility,
                          timestamp, createdAt, updatedAt))
  }
}
