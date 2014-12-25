import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {
  def webDriver = WebDriverFactory(classOf[PhantomJSDriver])

  "Application" should {
    "work from within a browser" in new WithBrowser(webDriver = webDriver) {
      browser.goTo("http://localhost:" + port)
      browser.pageSource must contain("GitHubスカウターオンライン")
    }
  }
}
