package org.requests

import com.ning.http.client.multipart.{ ByteArrayPart, FilePart, Part, StringPart }

import java.io.File
import java.nio.charset.Charset

sealed trait BodyPart {
  def toPart: Part
}

case class StringBodyPart(
  name: String,
  value: String,
  contentType: Option[String] = None,
  charset: Option[Charset] = None,
  contentId: Option[String] = None,
  transferEncoding: Option[String] = None) extends BodyPart {

  def toPart: StringPart = new StringPart(
    name,
    value,
    contentType.getOrElse(null),
    charset.getOrElse(null),
    contentId.getOrElse(null),
    transferEncoding.getOrElse(null))
}

case class FileBodyPart(
  name: String,
  file: File,
  contentType: Option[String] = None,
  charset: Option[Charset] = None,
  fileName: Option[String] = None,
  contentId: Option[String] = None,
  transferEncoding: Option[String] = None) extends BodyPart {

  def toPart: FilePart = new FilePart(
    name,
    file,
    contentType.getOrElse(null),
    charset.getOrElse(null),
    fileName.getOrElse(null),
    contentId.getOrElse(null),
    transferEncoding.getOrElse(null))
}

case class ByteArrayBodyPart(
  name: String,
  bytes: Array[Byte],
  contentType: Option[String] = None,
  charset: Option[Charset] = None,
  contentId: Option[String] = None,
  fileName: Option[String] = None,
  transferEncoding: Option[String] = None) extends BodyPart {

  def toPart: ByteArrayPart = new ByteArrayPart(
    name,
    bytes,
    contentType.getOrElse(null),
    charset.getOrElse(null),
    fileName.getOrElse(null),
    contentId.getOrElse(null),
    transferEncoding.getOrElse(null))
}
