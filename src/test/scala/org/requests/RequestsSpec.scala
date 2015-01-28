package org.requests

import java.nio.charset.StandardCharsets.UTF_8

import org.requests.Implicits._

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.{ FlatSpec, Matchers }

import play.api.libs.json.Json

class RequestsSpec extends FlatSpec
  with Matchers
  with ScalaFutures
  with TypeCheckedTripleEquals {

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val getUrl = "http://httpbin.org/get"
  val slowUrl = "http://httpbin.org/delay/1"
  val expiredCertUrl = "https://testssl-expire.disig.sk/index.en.html"
  val utf8 = "http://httpbin.org/encoding/utf8"

  s"""Requests.get("$getUrl")""" should "return the correct response" in {
    val requests = Requests()

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
      val data = Json.parse(r.content)
      val args = (data \ "args").as[Map[String, String]]
      val returnedHeaders = Map("Test-Header" -> Seq((data \ "headers" \ "Test-Header").as[String]))

      r.headers.get("Content-Type") should === (Some(Vector("application/json")))
      returnedHeaders should === (headers)
      args should === (params)
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }

  s"""Requests.get("$slowUrl")""" should "contain a TimeoutException when given a very short timeout (ms)" in {
    val requests = Requests()
    val result = requests.get(
      url = slowUrl,
      timeout = Some(1))

    whenReady(result.failed) { r =>
      r shouldBe a [java.util.concurrent.TimeoutException]
    }

    requests.close
  }

  s"""Requests.get("$utf8")""" should "return the correct encoding" in {
    val requests = Requests()
    val result = requests.get(url = utf8)

    whenReady(result) { r =>
      r.text should be ('defined)
      r.apparentEncoding() should === (Some(UTF_8)) // read from Content-type header
      r.apparentEncoding(true) should === (Some(UTF_8)) // Chardet
    }

    requests.close
  }

  s"""Requests.get("$expiredCertUrl")""" should "contain a ConnectException" in {
    val requests = Requests()
    val result = requests.get(url = expiredCertUrl)

    whenReady(result.failed) { r =>
      r shouldBe a [java.net.ConnectException]
    }

    requests.close
  }

  s"""Requests.get("$expiredCertUrl", verify = false)""" should "return a 200" in {
    val requests = Requests(verify = false)
    val result = requests.get(url = expiredCertUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }

}
