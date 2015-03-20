package org.requests

class OptionsSpec extends RequestsSpec {
  behavior of s"""options("$getUrl")"""

  it should "be an OK with appropriate Allow headers" in {
    val result = requests.options(url = getUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
      r.headers.get("Allow").flatMap(_.headOption) should be ('defined)
    }
  }
}

