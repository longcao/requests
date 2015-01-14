package org.requests

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{ Millis, Span, Seconds }
import org.scalatest.{ FlatSpec, Matchers }

class ExampleSpec extends FlatSpec
  with Matchers
  with ScalaFutures
  with TypeCheckedTripleEquals {

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val getUrl = new java.net.URL("http://httpbin.org/get")
  val utf8 = new java.net.URL("http://httpbin.org/encoding/utf8")

  s"""Requests.get("${getUrl.toString}")""" should "return a 200" in {
    val requests = new Requests()
    val result = requests.get(
      url = getUrl,
      data = ByteArrayData("hello".getBytes))

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }

  s"""Requests.get("${utf8.toString}")""" should "return the correct encoding" in {
    val requests = new Requests()
    val result = requests.get(url = utf8)

    whenReady(result) { r =>
      r.apparentEncoding should === (Some("UTF-8"))
    }

    requests.close
  }

}
