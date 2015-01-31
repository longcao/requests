package org.requests

class RedirectSpec extends RequestsSpec {
  private val redirectUrl = "http://httpbin.org/redirect/1"
  behavior of s"""get("$redirectUrl")"""

  it should "redirect correctly" in {
    val result = requests.get(url = redirectUrl)

    whenReady(result) { r =>
      r.url should === (getUrl)
      r.status should === (org.requests.status.OK)
    }
  }

  it should "not redirect if allowRedirects = false" in {
    val result = requests.get(url = redirectUrl, allowRedirects = false)

    whenReady(result) { r =>
      r.status should === (org.requests.status.Found)
    }
  }
}
