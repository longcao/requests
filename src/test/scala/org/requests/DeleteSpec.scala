package org.requests

class DeleteSpec extends RequestsSpec {
  behavior of s"""delete("$deleteUrl")"""

  it should "be an OK" in {
    val result = requests.delete(url = deleteUrl)

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }
  }
}

