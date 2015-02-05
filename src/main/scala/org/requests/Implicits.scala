package org.requests

import scala.language.implicitConversions

/**
 * Convenience implicit conversions for Requests.scala wrapper classes, employ at your leisure.
 *
 * {{{
 * import org.requests._
 * import org.requests.Implicits._
 *
 * val requests = Requests()
 * val data: Map[String, String] = Map("k" -> "v")
 * val result: Future[Response] = requests.post(url = "http://httpbin.org/post", data = data)
 * }}}
 *
 */
object Implicits {
  implicit def byteArray2ByteArrayData(ba: Array[Byte]): ByteArrayData = ByteArrayData(ba)
  implicit def string2StringData(s: String): StringData = StringData(s)
  implicit def map2FormData(m: Map[String, String]): FormData = FormData(m)
  implicit def listBodyPart2MultipartData(l: List[BodyPart]): MultipartData = MultipartData(l)
}
