package org.requests

import java.nio.charset.StandardCharsets.UTF_8

import org.requests.Implicits._

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{ Millis, Span, Seconds }
import org.scalatest.{ FlatSpec, Matchers }

class RequestsSpec extends FlatSpec
  with Matchers
  with ScalaFutures
  with TypeCheckedTripleEquals {

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val getUrl = new java.net.URL("http://httpbin.org/get")
  val expiredCertUrl = new java.net.URL("https://testssl-expire.disig.sk/index.en.html")
  val utf8 = new java.net.URL("http://httpbin.org/encoding/utf8")

  s"""Requests.get("${getUrl.toString}")""" should "return a 200" in {
    val requests = Requests()
    val result = requests.get(url = getUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }

  s"""Requests.get("${utf8.toString}")""" should "return the correct encoding" in {
    val requests = Requests()
    val result = requests.get(url = utf8)

    whenReady(result) { r =>
      r.apparentEncoding should === (Some(UTF_8))
    }

    requests.close
  }

  s"""Requests.get("${expiredCertUrl.toString}")""" should "contain a ConnectException" in {
    val requests = Requests()
    val result = requests.get(url = expiredCertUrl)

    whenReady(result.failed) { r =>
      r shouldBe a [java.net.ConnectException]
    }

    requests.close
  }

  s"""Requests.get("${expiredCertUrl.toString}"), verify = false""" should "return a 200" in {
    val requests = Requests(verify = false)
    val result = requests.get(url = expiredCertUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }

}
