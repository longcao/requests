package org.requests

import java.nio.charset.StandardCharsets.UTF_8

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
  val failUrl = new java.net.URL("https://kennethreitz.org/")
  val utf8 = new java.net.URL("http://httpbin.org/encoding/utf8")

  s"""Requests.get("${getUrl.toString}")""" should "return a 200" in {
    val requests = Requests()
    val result = requests.get(
      url = getUrl,
      data = ByteArrayData("hello".getBytes))

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

  s"""Requests.get("${failUrl.toString}")""" should "contain a HostnameVerifier exception" in {
    val requests = Requests()
    val result = requests.get(url = failUrl)

    whenReady(result.failed) { r =>
      r shouldBe a [java.net.ConnectException]
    }

    requests.close
  }

}
