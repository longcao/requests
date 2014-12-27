package org.requests

import com.ning.http.client.{ Response => NingResponse }

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._

trait Response {
  def apparentEncoding: String
  def content: Array[Byte]
  def cookies: Seq[Cookie]
  def elasped: Long
  def encoding: String
  def headers: Map[String, Seq[String]]
  def history: Seq[Response]
  def isPermanentRedirect: Boolean
  def isRedirect: Boolean
  //def iterContent
  //def iterLines
  def json: String
  //def links
  //def raw
  def reason: String
  //def request
  def statusCode: Int
  def text: String
  def url: String
}

object Response {
  def apply(nr: NingResponse): Response = {
    val headers: Map[String, Seq[String]] =
      mapAsScalaMapConverter(nr.getHeaders).asScala
        .map { case (k ,v) => k -> v.asScala.to[Seq] }
        .toMap

    ResponseImpl(
      content = nr.getResponseBodyAsBytes,
      headers = headers,
      isRedirect = nr.isRedirected,
      reason = nr.getStatusText,
      statusCode = nr.getStatusCode,
      url = nr.getUri.toString
    )
  }
}

case class ResponseImpl(
  content: Array[Byte],
  cookies: Seq[Cookie] = Seq.empty,
  elasped: Long = 0L,
  encoding: String = "placeholder",
  headers: Map[String, Seq[String]] = Map.empty,
  history: Seq[Response] = Seq.empty,
  isRedirect: Boolean,
  //iterContent,
  //iterLines,
  //links,
  //raw,
  reason: String,
  //request,
  statusCode: Int,
  url: String) extends Response {

  lazy val apparentEncoding: String = "placeholder"
  // TODO: replace this with status code types
  lazy val isPermanentRedirect: Boolean = statusCode == 301 || statusCode == 308
  lazy val json: String = "placeholder"
  lazy val text: String = "placeholder"
}

