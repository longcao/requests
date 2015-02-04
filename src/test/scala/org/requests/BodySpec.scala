package org.requests

import org.requests.Implicits._

import play.api.libs.json.Json

class BodySpec extends RequestsSpec {
  behavior of s"""post("$postUrl")"""

  it should "be an OK and return the byte array body" in {
    val data: Array[Byte] = "hello".getBytes
    val result = requests.post(url = postUrl, data = data)

    whenReady(result) { r =>
      val returned = Json.parse(r.content)
      val d = (returned \ "data").as[String]
      d.getBytes should === (data)
    }
  }

  it should "be an OK and return the string" in {
    val data: String = "hello"
    val result = requests.post(url = postUrl, data = data)

    whenReady(result) { r =>
      val returned = Json.parse(r.content)
      val d = (returned \ "data").as[String]
      d should === (data)
    }
  }

  it should "be an OK and return the form data" in {
    val data: Map[String, String] = Map("key1" -> "value1")
    val result = requests.post(url = postUrl, data = data)

    whenReady(result) { r =>
      val returned = Json.parse(r.content)
      val d = (returned \ "form").as[Map[String, String]]
      d should === (data)
    }
  }

  it should "be an OK and return the multipart data" in {
    val (stringName, stringValue) = "strName" -> "strValue"
    val stringBodyPart = StringBodyPart(name = stringName, value = stringValue)

    val fileName = "file1"
    val file = new java.io.File(getClass.getResource("/filebody.txt").getPath)
    val fileBodyPart = FileBodyPart(
      name = fileName,
      file = file)

    val (byteArrayName, byteArrayValue) = "baName" -> "baValue".getBytes
    val byteArrayBodyPart = ByteArrayBodyPart(name = byteArrayName, bytes = byteArrayValue)

    val data = List(stringBodyPart, fileBodyPart, byteArrayBodyPart)

    val result = requests.post(url = postUrl, data = data)

    whenReady(result) { r =>
      val returned = Json.parse(r.content)
      val form = (returned \ "form").as[Map[String, String]]
      val files = (returned \ "files").as[Map[String, String]]

      form(stringName) should === (stringValue)
      form(byteArrayName) should === (new String(byteArrayValue))
      new String(java.nio.file.Files.readAllBytes(file.toPath())) should === (files(fileName))
    }
  }
}


