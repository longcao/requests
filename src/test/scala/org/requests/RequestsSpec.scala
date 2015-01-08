package org.requests

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FlatSpec, Matchers }
import org.scalactic.TypeCheckedTripleEquals

class ExampleSpec extends FlatSpec
  with Matchers
  with ScalaFutures
  with TypeCheckedTripleEquals {

  val getUrl = new java.net.URL("http://httpbin.org/status/200")

  s"""Requests.get("${getUrl.toString}")""" should "return a 200" in {
    val requests = new Requests()
    val result = requests.get(getUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.OK)
    }

    requests.close
  }

}
