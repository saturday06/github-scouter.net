package models

import com.google.common.base.Charsets
import com.jolbox.bonecp.UsernamePassword
import org.joda.time.DateTime
import play.Play
import play.api.db.slick.Config.driver.simple._
import com.github.tototoshi.slick.MySQLJodaSupport._
import play.api.libs.json.{JsResultException, Json}

import scala.sys.process.{Process, ProcessLogger}

case class PowerLevel(id: Long, userName: String, attack: BigDecimal, intelligence: BigDecimal, agility: BigDecimal,
                       timestamp: Long, createdAt: DateTime, updatedAt: DateTime)

/**
 * DRYにできんのか
 */
class PowerLevelsTable(tag: Tag) extends Table[PowerLevel](tag, "power_levels") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userName = column[String]("user_name", O.NotNull)
  def attack = column[BigDecimal]("attack", O.NotNull)
  def intelligence = column[BigDecimal]("intelligence", O.NotNull)
  def agility = column[BigDecimal]("agility", O.NotNull)
  def timestamp = column[Long]("timestamp", O.NotNull) // TODO: DateTimeにする
  def createdAt = column[DateTime]("created_at", O.NotNull)
  def updatedAt = column[DateTime]("updated_at", O.NotNull)

  def * = (id, userName, attack, intelligence, agility, timestamp, createdAt, updatedAt) <> (PowerLevel.tupled, PowerLevel.unapply _)
}

/**
 * 単数形のが良いだろうか
 */
object PowerLevels {
  def findOrCreateByUserName(userName: String): Option[PowerLevel] = {
    // これらの処理はフラットに縦にならべられないか
    val query = TableQuery[PowerLevelsTable]
      .filter(_.userName === userName) // TODO: 大文字小文字マッチ必要かも？
      .sortBy(_.timestamp.desc)
      .sortBy(_.id.desc) // 上とくっつけらんないか？

    // 最近のものが存在したら返す
    val fromTimestamp = DateTime.now().minusDays(3 /* TODO: 定数 */).getMillis / 1000
    query.filter(_.timestamp > fromTimestamp).firstOption.map { found =>
      return Some(found)
    }

    // ない場合はスカウターを実行
    PowerLevels.executeScouter(userName).map { found =>
      TableQuery[PowerLevelsTable].insert(found) // TODO: テーブルロック待ったなし
      return Some(found)
    }

    // 全検索して返す
    query.firstOption
  }

  def validUserName(userName: String): Boolean = {
    // 「コマンドラインで使える文字列」型があると嬉しい
    val asciiUserName = new String(userName.getBytes(Charsets.US_ASCII), Charsets.US_ASCII)
    asciiUserName.matches("""\p{Graph}{0,2000}""")
  }

  def fromJSONString(jsonString: String, userName: String /* TODO: これどーすっか */): Option[PowerLevel] = {
    // TODO: よりエレガントなウェイを使う。
    try {
      val json = Json.parse(jsonString)
      val id = 0L // これで良いのか？
      val attack = json.\("attack").as[BigDecimal]
      val intelligence = json.\("intelligence").as[BigDecimal]
      val agility = json.\("agility").as[BigDecimal]
      val timestamp = json.\("timestamp").as[Long]
      val createdAt = DateTime.now()
      val updatedAt = createdAt
      Some(PowerLevel(id, userName, attack, intelligence, agility,
        timestamp, createdAt, updatedAt))
    } catch {
      case e: JsResultException => None // TODO: エラー情報が消えるの良くない。eitherかscalazのvalidation
    }
  }

  /**
   * ここに書くんじゃなくて適当なサービスクラスっぽいものを用意
   */
  def executeScouter(userName: String): Option[PowerLevel] = {
    if (!validUserName(userName)) {
      return None
    }
    val process = Process(Seq(
      "./node_modules/.bin/github-scouter",
      userName,
      "--json",
      "--no-cache",
      "--token",
      Play.application.configuration.getString("githubscouter.token")
    ), Play.application.path)
    val out = new StringBuilder
    val err = new StringBuilder
    val exitStatus = process.!(ProcessLogger(
      (o: String) => out.append(o),
      (e: String) => err.append(e)))
    if (exitStatus != 0) {
      return None
    }
    fromJSONString(out.toString(), userName)
  }
}
