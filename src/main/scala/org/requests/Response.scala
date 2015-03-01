package org.requests

import com.ning.http.client.{ Response => AHCResponse }

import java.nio.charset.Charset

import org.requests.chardet.Chardet
import org.requests.status._

import scala.collection.immutable.{ Seq, TreeMap }
import scala.collection.JavaConverters._
import scala.util.{ Failure, Success, Try }

object Response {

  /**
   * Constructs a Response from the java AHCResponse.
   */
  def construct(
    ahcr: AHCResponse,
    elasped: Long,
    request: Request): Response = {

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
      elasped = elasped,
      headers = headers,
      isRedirect = ahcr.isRedirected,
      request = request,
      status= status,
      url = ahcr.getUri.toString
    )
  }
}

case class Response(
  content: Array[Byte],
  cookies: Seq[Cookie],
  elasped: Long,
  headers: Map[String, Seq[String]],
  //history: Seq[Response] = Seq.empty,
  isRedirect: Boolean,
  //iterContent,
  //iterLines,
  //raw,
  request: Request,
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
   *
   * @param skipHeader skip attempting to parse the "Content-Type" header
   * @return the inferred Charset, if any.
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

  lazy val text: Option[String] = apparentEncoding().map(new String(content, _))

  /**
   * Parses the Link header, if any, and exposes it as a Map(rel -> url).
   */
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

