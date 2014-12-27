import org.requests._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val url = new java.net.URL("http://longcao.org")

  val headers = Map("Accept-Language" -> Seq("en-US"))
  val cookies = Seq(
    CookieImpl(
      domain = "asdf",
      expires = None,
      maxAge = None,
      name = Some("hello"),
      path = "/",
      secure = false,
      value = Some("value")),
    CookieImpl(
      domain = "asdf",
      expires = None,
      maxAge = None,
      name = Some("goodbye"),
      path = "/",
      secure = false,
      value = Some("value2")))

  val r = Await.result(Requests.get(url, cookies = cookies, headers = headers), 2.seconds)
  println(r)
  System.exit(0)
}
