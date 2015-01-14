package org.requests

sealed trait Data
case object EmptyData extends Data
case class ByteArrayData(value: Array[Byte])      extends Data
case class StringData(value: String)              extends Data
case class FormData(values: Map[String, String])  extends Data
case class MultipartData(files: List[BodyPart])   extends Data
