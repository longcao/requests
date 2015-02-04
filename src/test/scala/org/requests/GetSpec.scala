package org.requests

import org.requests.Implicits._

import play.api.libs.json.Json

class GetSpec extends RequestsSpec {
  behavior of s"""get("$getUrl")"""

  it should "return the correct response" in {
    val params = Map("k1" -> "v1", "k2" -> "v2")
    val headers = Map("Test-Header" -> Seq("zxcv"))
    val cookies = Seq(
      Cookie(
        domain = "httpbin.org",
        expires = Some(5),
        httpOnly = true,
        maxAge = Some(10),
        name = Some("hello"),
        path = "/",
        secure = false,
        value = Some("value")))

    val result = requests.get(
      url = getUrl,
      headers = headers,
      cookies = cookies,
      params = params)

    whenReady(result) { r =>
      val data = Json.parse(r.json)
      val args = (data \ "args").as[Map[String, String]]
      val returnedHeaders = Map("Test-Header" -> Seq((data \ "headers" \ "Test-Header").as[String]))

      r.headers.get("Content-Type") should === (Some(Vector("application/json")))
      returnedHeaders should === (headers)
      args should === (params)
      r.status should === (org.requests.status.OK)
    }
  }
}