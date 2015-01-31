package org.requests

class SslRequestSpec extends RequestsSpec {
  private val expiredCertUrl = "https://testssl-expire.disig.sk/index.en.html"

  behavior of s"""get("$expiredCertUrl")"""

  it should "contain a ConnectException" in {
    val result = requests.get(url = expiredCertUrl)

    whenReady(result.failed) { r =>
      r shouldBe a [java.net.ConnectException]
    }
  }

  it should "return a 200 if verify = false" in {
    val requests = Requests(verify = false)
    val result = requests.get(url = expiredCertUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }

    requests.close
  }
}
