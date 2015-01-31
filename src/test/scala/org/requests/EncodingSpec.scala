package org.requests

import java.nio.charset.StandardCharsets.UTF_8

class EncodingSpec extends RequestsSpec {
  private val utf8Url = "http://httpbin.org/encoding/utf8"
  behavior of s"""get("$utf8Url")"""

  it should "return the correct encoding" in {
    val result = requests.get(url = utf8Url)

    whenReady(result) { r =>
      r.text should be ('defined)
      r.apparentEncoding() should === (Some(UTF_8)) // read from Content-type header
      r.apparentEncoding(true) should === (Some(UTF_8)) // Chardet
    }
  }

}
