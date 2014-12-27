import org.requests.Requests

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val url = new java.net.URL("http://longcao.org")

  val headers = Map("Accept-Language" -> Seq("en-US"))

  val r = Await.result(Requests.get(url, headers = headers), 2.seconds)
  println(r)
  System.exit(0)
}
