package models

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import com.github.tototoshi.slick.MySQLJodaSupport._

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
