import controllers.PowerLevelsController
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class PowerLevelsControllerSpec extends Specification {
  PowerLevelsController.toString should {
    "shows god's power level" in new WithApplication{
      val god = route(FakeRequest(GET, "/powerLevels/god")).get
      status(god) must equalTo(OK)

      // TODO: JSON matcher required
      contentType(god) must beSome.which(_ == "application/json")
      contentAsString(god) must contain ("userName")
    }
  }
}
