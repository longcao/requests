package org.requests

class LinkHeaderSpec extends RequestsSpec {
  private val linkUrl = "https://api.github.com/users/longcao/repos?page=1&per_page=1"
  behavior of s"""get("$linkUrl")"""

  it should "correctly parse Link headers in the response" in {
    val result = requests.get(url = linkUrl)

    whenReady(result) { r =>
      r.links should not be empty
      r.links.get("next") should be ('defined)
    }
  }
}
