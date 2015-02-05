package org.requests

import com.ning.http.client.{ Response => AHCResponse }

import java.nio.charset.Charset

import org.requests.chardet.Chardet
import org.requests.status._

import scala.collection.immutable.{ Seq, TreeMap }
import scala.collection.JavaConverters._
import scala.util.{ Failure, Success, Try }

object Response {
  def apply(ahcr: AHCResponse): Response = {
    val headers: Map[String, Seq[String]] = {
      val hs = mapAsScalaMapConverter(ahcr.getHeaders).asScala
        .toSeq
        .map { case (k ,v) => k -> v.asScala.to[Seq] }

      TreeMap(hs: _*)(new Ordering[String] {
        def compare(x: String, y: String): Int = x.compareToIgnoreCase(y)
      })
    }

    val status: Status = Try(Status.codesToStatus(ahcr.getStatusCode)) match {
      case Success(status) => status
      case Failure(ex) => Unknown(ahcr.getStatusCode, ahcr.getStatusText)
    }

    Response(
      content = ahcr.getResponseBodyAsBytes,
      cookies = ahcr.getCookies.asScala.to[Seq].map(Cookie(_)),
      headers = headers,
      isRedirect = ahcr.isRedirected,
      status= status,
      url = ahcr.getUri.toString
    )
  }
}

case class Response(
  content: Array[Byte],
  cookies: Seq[Cookie] = Seq.empty,
  //elasped: Long = 0L,
  headers: Map[String, Seq[String]] = Map.empty,
  //history: Seq[Response] = Seq.empty,
  isRedirect: Boolean,
  //iterContent,
  //iterLines,
  //raw,
  //request,
  status: Status,
  url: String) {

  private def parseCharset(ct: String): Option[Charset] = {
    ct.split(";")
      .map(_.trim)
      .filter(_.startsWith("charset="))
      .map(_.stripPrefix("charset="))
      .headOption
      .flatMap { nameToCharset(_) }
  }

  private def nameToCharset(name: String): Option[Charset] = Try(Charset.forName(name)).toOption

  /**
   * Attempts to get the charset from the 'Content-Type' header if available,
   * then attempts to detect the charset from the content bytes.
   */
  def apparentEncoding(skipHeader: Boolean = false): Option[Charset] = {
    if (skipHeader) {
      Chardet.detectEncoding(content).flatMap(nameToCharset(_))
    } else {
      headers.get("Content-Type")
        .flatMap(_.headOption match {
          case Some(v) => parseCharset(v)
          case _ => Chardet.detectEncoding(content).flatMap(nameToCharset(_))
        })
    }
  }

  lazy val isPermanentRedirect: Boolean = status == MovedPermanently || status == PermanentRedirect

  /**
   * This isn't really JSON but to avoid forcing a specific JSON library
   * as a dependency you should use your choice JSON.parse() call.
   */
  lazy val json: String = new String(content)
  def text: Option[String] = apparentEncoding().map(new String(content, _))

  lazy val links: Map[String, String] = {
    headers.getOrElse("Link", Seq.empty)
      .flatMap(_.split(", "))
      .flatMap { link =>
        val angles = """<(.*?)>""".r
        val quotes = """\"(.*?)\"""".r

        for {
          url <- angles.findFirstMatchIn(link)
          rel <- quotes.findFirstMatchIn(link)
        } yield ((rel.group(1), url.group(1)))
      }.toMap
  }

  lazy val reason = status.reason
  lazy val statusCode = status.code
}

