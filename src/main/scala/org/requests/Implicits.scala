package org.requests

import scala.language.implicitConversions

/**
  * Convenience implicit conversions for Requests.scala wrapper classes,
  * `import org.requests.Implicits._` at your leisure.
  */
object Implicits {
  implicit def byteArray2ByteArrayData(ba: Array[Byte]): ByteArrayData = ByteArrayData(ba)
  implicit def string2StringData(s: String): StringData = StringData(s)
  implicit def map2FormData(m: Map[String, String]): FormData = FormData(m)
  implicit def listBodyPart2MultipartData(l: List[BodyPart]): MultipartData = MultipartData(l)
}
