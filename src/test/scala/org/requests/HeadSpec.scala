package org.requests

class HeadSpec extends RequestsSpec {
  val getUrl = "http://httpbin.org/get"

  s"""head("$getUrl")""" should "return the correct response with no body" in {
    val result = requests.head(url = getUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
      r.headers should not be empty
      r.content shouldBe empty
    }
  }
}
