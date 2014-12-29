import controllers.PowerLevelsController
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class PowerLevelsControllerSpec extends Specification {
  PowerLevelsController.getClass.getName should {
    "show hello's power level" in new WithApplicationAndMockGitHub {
      val hello = route(FakeRequest(GET, "/powerLevels/hello")).get
      status(hello) must equalTo(OK)

      // TODO: JSON matcher required
      contentType(hello) must beSome.which(_ == "application/json")
      contentAsString(hello) must contain("userName")
    }
  }
}
