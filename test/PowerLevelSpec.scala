import models.{PowerLevel, PowerLevels, PowerLevelsTable}
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.slick.lifted.TableQuery

/**
 * TODO: いちいちdeleteしているのと、DB.withSessionをまとめる
 */
@RunWith(classOf[JUnitRunner])
class PowerLevelSpec extends Specification {
  PowerLevels.getClass.getName should {
    "be generated from json string" in {
      val obj = PowerLevels.fromJSONString(
        """
          |{"attack":1,"intelligence":2,"agility":3,"cached":false,"timestamp":4}
        """
          .stripMargin, "a").get

      obj.attack must_== 1
      obj.intelligence must_== 2
      obj.agility must_== 3
      obj.timestamp must_== 4
    }

    "not be generated from invalid json string" in {
      PowerLevels.fromJSONString("{}", "foo") must be(None)
    }

    "detect valid github's user name" in {
      PowerLevels.validUserName("foo") must beTrue
    }

    "detect invalid github's user name" in {
      PowerLevels.validUserName("aaa\nbbb") must beFalse
    }

    "execute github-scouter.js" in new WithApplicationAndMockGitHub {
      DB.withSession { implicit session =>
        val hello = PowerLevels.executeScouter("hello").get
        hello.userName must be("hello")
      }
    }

    "read recent cache" in new WithApplicationAndMockGitHub {
      DB.withSession { implicit session =>
        val now = DateTime.now()
        val timestamp = now.minusHours(3).getMillis / 1000
        val cache = PowerLevel(1, "hello", 100, 110, 120, timestamp, now, now)
        TableQuery[PowerLevelsTable].delete
        TableQuery[PowerLevelsTable].insert(cache)

        val powerLevel = PowerLevels.findOrCreateByUserName("hello").get
        powerLevel.attack.toInt must_== 100 // TODO:
        powerLevel.intelligence.toInt must_== 110
        powerLevel.agility.toInt must_== 120
      }
    }

    "read more recent cache" in new WithApplicationAndMockGitHub {
      DB.withSession { implicit session =>
        val now = DateTime.now()
        val oldTimestamp = now.minusHours(4).getMillis / 1000
        val newTimestamp = now.minusHours(3).getMillis / 1000
        val oldCache = PowerLevel(1, "hello", 100, 110, 120, oldTimestamp, now, now)
        val newCache = PowerLevel(2, "hello", 200, 210, 220, newTimestamp, now, now)
        TableQuery[PowerLevelsTable].delete
        TableQuery[PowerLevelsTable].insert(oldCache)
        TableQuery[PowerLevelsTable].insert(newCache)

        val powerLevel = PowerLevels.findOrCreateByUserName("hello").get
        powerLevel.attack.toInt must_== 200 // TODO:
        powerLevel.intelligence.toInt must_== 210
        powerLevel.agility.toInt must_== 220
      }
    }

    "not read old cache and call github api" in new WithApplicationAndMockGitHub {
      DB.withSession { implicit session =>
        val now = DateTime.now()
        val timestamp = now.minusDays(7).getMillis / 1000
        val cache = PowerLevel(1, "hello", 100, 110, 120, timestamp, now, now)
        TableQuery[PowerLevelsTable].delete
        TableQuery[PowerLevelsTable].insert(cache)

        val powerLevel = PowerLevels.findOrCreateByUserName("hello").get
        powerLevel.attack.toInt must_== 43 // TODO:
        powerLevel.intelligence.toInt must_== 6
        powerLevel.agility.toInt must_== 68
      }
    }

    "read old cache if the request to github is failed" in new WithApplicationAndMockGitHub {
      DB.withSession { implicit session =>
        val now = DateTime.now()
        val timestamp = now.minusDays(7).getMillis / 1000
        val cache = PowerLevel(1, "hellox", 100, 110, 120, timestamp, now, now)
        TableQuery[PowerLevelsTable].delete
        TableQuery[PowerLevelsTable].insert(cache)

        val powerLevel = PowerLevels.findOrCreateByUserName("hellox").get
        powerLevel.attack.toInt must_== 100 // TODO:
        powerLevel.intelligence.toInt must_== 110
        powerLevel.agility.toInt must_== 120
      }
    }

    "fail to read power level if no cache exists and the request to github is failed" in new WithApplicationAndMockGitHub {
      DB.withSession { implicit session =>
        TableQuery[PowerLevelsTable].delete
        PowerLevels.findOrCreateByUserName("hellox") must beNone
      }
    }
  }
}
