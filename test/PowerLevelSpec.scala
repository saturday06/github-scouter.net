import models.{PowerLevels, PowerLevel}
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class PowerLevelSpec extends Specification {
  PowerLevels.toString should {
    "is generated from json string" in {
      val obj = PowerLevels.fromJSONString(
        """
          |{"attack":1,"intelligence":2,"agility":3,"cached":false,"timestamp":4}
        """
        .stripMargin, "a").get

      // TODO: 誤差有りマッチ
      // obj.attack must be(BigDecimal(1))
      // obj.intelligence must be(BigDecimal(2))
      // obj.agility must be(BigDecimal(3))
      Long.box(obj.timestamp) must be(Long.box(4L)) // TODO: !?
    }

    "isn't generated from invalid json string" in {
      PowerLevels.fromJSONString("{}", "foo") must be(None)
    }

    "detects valid github's user name" in {
      PowerLevels.validUserName("foo") must beTrue
    }

    "detects invalid github's user name" in {
      PowerLevels.validUserName("aaa\nbbb") must beFalse
    }

    "executes github-scouter.js" in new WithApplication {
      val god = PowerLevels.executeScouter("god").get
      god.userName must be("god")
    }
  }
}
