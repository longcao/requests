package org.requests

class TimeoutSpec extends RequestsSpec {
  private val slowUrl = "http://httpbin.org/delay/1"
  behavior of s"""get("$slowUrl")"""

  it should "contain a TimeoutException when given a very short timeout in ms" in {
    val result = requests.get(
      url = slowUrl,
      timeout = Some(1))

    whenReady(result.failed) { r =>
      r shouldBe a [java.util.concurrent.TimeoutException]
    }
  }
}

