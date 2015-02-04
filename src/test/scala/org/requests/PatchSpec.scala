package org.requests

import org.requests.Implicits._

import play.api.libs.json.Json

class PatchSpec extends RequestsSpec {
  behavior of s"""patch("$patchUrl")"""

  it should "be an OK and return the correct data" in {
    val data = "hello patch"
    val result = requests.patch(url = patchUrl, data = data)

    whenReady(result) { r =>
      val returned = Json.parse(r.content)
      val d = (returned \ "data").as[String]

      d should === (data)
      r.status should === (org.requests.status.OK)
    }
  }
}


