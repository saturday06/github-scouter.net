import org.specs2.execute.{AsResult, Result}
import play.api.test.WithApplication

import scala.sys.process.Process

/**
 * モックサーバー起動部分だけ別にするべきだが・・・
 */
abstract class WithApplicationAndMockGitHub extends WithApplication {
  override def around[T](t: => T)(implicit evidence$1: AsResult[T]): Result = {
    val mockServerProcess = Process(Seq("./node_modules/.bin/gulp", "mock-server")).run
    try {
      super.around(t)
    } finally {
      mockServerProcess.destroy()
    }
  }
}
