package org.requests

/**
 * Data is an algebraic data type that wraps more basic types for message bodies.
 */
sealed trait Data
case object EmptyData extends Data
case class ByteArrayData(value: Array[Byte])      extends Data
case class StringData(value: String)              extends Data
case class FormData(values: Map[String, String])  extends Data
case class MultipartData(files: List[BodyPart])   extends Data
