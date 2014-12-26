import org.requests.Requests

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

object Main extends App {
  val url = new java.net.URL("http://longcao.org")

  val r = Await.result(Requests.get(url), 2.seconds)
  println(r)
  System.exit(0)
}
