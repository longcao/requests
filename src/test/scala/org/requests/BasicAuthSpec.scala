package org.requests

class BasicAuthSpec extends RequestsSpec {
  private val basicAuthUrl = "http://httpbin.org/basic-auth/user/passwd"
  behavior of s"""get("$basicAuthUrl")"""

  it should "HTTP Basic auth successfully" in {
    val auth = Auth(user = "user", password = "passwd", AuthScheme.BASIC)
    val result = requests.get(
      url = basicAuthUrl,
      auth = Some(auth))

    whenReady(result) { r =>
      r.status should === (org.requests.status.OK)
    }
  }

  it should "fail HTTP Basic auth if incorrect" in {
    val auth = Auth(user = "notauser", password = "thisisbad", AuthScheme.BASIC)
    val result = requests.get(
      url = basicAuthUrl,
      auth = Some(auth))

    whenReady(result) { r =>
      r.status should === (org.requests.status.Unauthorized)
    }
  }

}

