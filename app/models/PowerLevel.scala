package models

import play.api.db.slick.Config.driver.simple._

case class PowerLevel(name: Int, intelligence: Int, agility: Int)

class PowerLevelsTable(tag: Tag) extends Table[PowerLevel](tag, "PowerLevel") {
  def attack = column[Int]("attack", O.NotNull)
  def intelligence = column[Int]("intelligence", O.NotNull)
  def agility = column[Int]("agility", O.NotNull)
  
  def * = (attack, intelligence, agility) <> (PowerLevel.tupled, PowerLevel.unapply _)
}
