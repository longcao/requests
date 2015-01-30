package org.requests

import java.nio.charset.StandardCharsets.UTF_8

import org.requests.Implicits._

import play.api.libs.json.Json

class GetSpec extends RequestsSpec {
  private val slowUrl        = "http://httpbin.org/delay/1"
  private val expiredCertUrl = "https://testssl-expire.disig.sk/index.en.html"
  private val utf8Url        = "http://httpbin.org/encoding/utf8"
  private val redirectUrl    = "http://httpbin.org/redirect/1"

  s"""get("$getUrl")""" should "return the correct response" in {
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

  s"""get("$slowUrl")""" should "contain a TimeoutException when given a very short timeout (ms)" in {
    val result = requests.get(
      url = slowUrl,
      timeout = Some(1))

    whenReady(result.failed) { r =>
      r shouldBe a [java.util.concurrent.TimeoutException]
    }
  }

  s"""get("$redirectUrl")""" should "redirect correctly" in {
    val result = requests.get(url = redirectUrl)

    whenReady(result) { r =>
      r.url should === (getUrl)
      r.status should === (org.requests.status.OK)
    }
  }

  s"""get("$redirectUrl", allowRedirects = false)""" should "not redirect" in {
    val result = requests.get(url = redirectUrl, allowRedirects = false)

    whenReady(result) { r =>
      r.status should === (org.requests.status.Found)
    }
  }

  s"""get("$utf8Url")""" should "return the correct encoding" in {
    val result = requests.get(url = utf8Url)

    whenReady(result) { r =>
      r.text should be ('defined)
      r.apparentEncoding() should === (Some(UTF_8)) // read from Content-type header
      r.apparentEncoding(true) should === (Some(UTF_8)) // Chardet
    }
  }

  s"""get("$expiredCertUrl")""" should "contain a ConnectException" in {
    val result = requests.get(url = expiredCertUrl)

    whenReady(result.failed) { r =>
      r shouldBe a [java.net.ConnectException]
    }
  }

  s"""get("$expiredCertUrl"), verify = false""" should "return a 200" in {
    val requests = Requests(verify = false)
    val result = requests.get(url = expiredCertUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }
}
