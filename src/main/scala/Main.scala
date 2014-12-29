import org.requests._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val url = new java.net.URL("http://longcao.org")

  val headers = Map("Accept-Language" -> Seq("en-US"))
  val cookies = Seq(
    Cookie(
      domain = "asdf",
      expires = None,
      httpOnly = true,
      maxAge = None,
      name = Some("hello"),
      path = "/",
      secure = false,
      value = Some("value")),
    Cookie(
      domain = "asdf",
      expires = None,
      httpOnly = true,
      maxAge = None,
      name = Some("goodbye"),
      path = "/",
      secure = false,
      value = Some("value2")))

  val requests = new Requests()

  val r = Await.result(requests.get(url, cookies = cookies, headers = headers), 2.seconds)
  println(r)

  // close the underlying client
  requests.close
  System.exit(0)
}
