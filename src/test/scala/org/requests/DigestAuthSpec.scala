package org.requests

import org.scalatest.DoNotDiscover

//import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover class DigestAuthSpec extends RequestsSpec {
  private val digestAuthUrl = "http://httpbin.org/digest-auth/auth/user/passwd"
  behavior of s"""get("$digestAuthUrl")"""

  /*
  it should "HTTP Digest auth successfully" in {
    val auth = Auth(user = "user", password = "passwd", AuthScheme.DIGEST)
    val c = Cookie(
      domain = "httpbin.org",
      expires = Some(5),
      httpOnly = true,
      maxAge = Some(10),
      name = Some("fake"),
      path = "/",
      secure = false,
      value = Some("fake_value"))

    val challenge = requests.get(
      url = digestAuthUrl,
      cookies = Seq(c),
      auth = Some(auth))

    val result = challenge.flatMap { r =>
      val wwwAuth = r.headers.get("WWW-Authenticate").flatMap(_.headOption)
      val auth = wwwAuth.map { h =>
        Auth(
          user = "user",
          password = "passwd",
          scheme = AuthScheme.DIGEST,
          header = h)
      }

      //println(r.cookies)

      requests.get(
        url = digestAuthUrl,
        cookies = r.cookies,
        auth = auth)
    }

    whenReady(result) { r =>
      //r.printStackTrace
      println(r.json)
      println(r)
      r.status should === (org.requests.status.OK)
    }
  }

  it should "fail HTTP Digest auth if incorrect" in {
    val auth = Auth(user = "notauser", password = "thisisbad", AuthScheme.DIGEST)
    val result = requests.get(
      url = digestAuthUrl,
      auth = Some(auth))

    whenReady(result) { r =>
      println(r)
      r.status should === (org.requests.status.Unauthorized)
    }
  }
  */

}

