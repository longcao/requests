package org.requests

class HeadSpec extends RequestsSpec {
  behavior of s"""head("$getUrl")"""

  it should "be an OK with headers but no message body" in {
    val result = requests.head(url = getUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
      r.headers should not be empty
      r.content shouldBe empty
    }
  }

  it should "have the same headers as a GET request" in {
    val getResult  = requests.get(url = getUrl)
    val headResult = requests.head(url = getUrl)

    whenReady(getResult.zip(headResult)) { case (r1, r2) =>
      r1.headers should === (r2.headers)
    }
  }
}
