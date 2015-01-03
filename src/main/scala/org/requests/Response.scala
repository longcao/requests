package org.requests

import com.ning.http.client.{ Response => NingResponse }

import scala.collection.immutable.Seq
import scala.collection.JavaConverters._

import scala.util.{ Failure, Success, Try }

object Response {
  def apply(nr: NingResponse): Response = {
    val headers: Map[String, Seq[String]] =
      mapAsScalaMapConverter(nr.getHeaders).asScala
        .map { case (k ,v) => k -> v.asScala.to[Seq] }
        .toMap

    val statusCode: Status = Try(Status.codesToStatus(nr.getStatusCode)) match {
      case Success(status) => status
      case Failure(ex) => Unknown(nr.getStatusCode, nr.getStatusText)
    }

    Response(
      content = nr.getResponseBodyAsBytes,
      cookies = nr.getCookies.asScala.to[Seq].map(Cookie(_)),
      headers = headers,
      isRedirect = nr.isRedirected,
      reason = nr.getStatusText,
      statusCode = statusCode,
      url = nr.getUri.toString
    )
  }
}

case class Response(
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
  statusCode: Status,
  url: String) {

  lazy val apparentEncoding: String = "placeholder"
  lazy val isPermanentRedirect: Boolean = statusCode == MovedPermanently || statusCode == PermanentRedirect

  lazy val json: String = "placeholder"
  lazy val text: String = "placeholder"
}

