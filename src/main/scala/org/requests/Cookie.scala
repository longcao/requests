package org.requests

import com.ning.http.client.cookie.{ Cookie => NingCookie }

trait Cookie {
  def domain: String
  def expires: Option[Long]
  def maxAge: Option[Int]
  def name: Option[String]
  def path: String
  def secure: Boolean
  def value: Option[String]
}

object Cookie {
  def apply(nc: NingCookie): Cookie = {
    CookieImpl(
      domain = nc.getDomain,
      expires = if (nc.getExpires == -1) None else Some(nc.getExpires),
      maxAge = if (nc.getMaxAge == -1) None else Some(nc.getMaxAge),
      name = if (nc.getName.isEmpty) None else Some(nc.getName),
      path = nc.getPath,
      secure = nc.isSecure,
      value = if (nc.getValue.isEmpty) None else Some(nc.getValue))
  }

  /**
   * Converts a Seq[Cookie] into a flattened cookie header in the form of:
   * "Cookie" -> "name1=value2; name2=value2;" etc
   *
   * This is used mainly because the conversion from Cookie -> NingCookie
   * and using RequestBuilder.addCookie is trickier than just adding cookie headers.
   */
  def cookiesToHeader(cookies: Seq[Cookie]): Option[(String, Seq[String])] = {
    val cookieHeaderPairs: Seq[String] = for {
      cookie <- cookies
      name <- cookie.name
      value <- cookie.value
      pair <- Some(s"$name=$value")
    } yield (pair)

    if (cookieHeaderPairs.isEmpty)
      None
    else
      Some("Cookie" -> Seq(cookieHeaderPairs.mkString("; ") + ";"))
  }
}

case class CookieImpl(
  domain: String,
  expires: Option[Long],
  maxAge: Option[Int],
  name: Option[String],
  path: String,
  secure: Boolean,
  value: Option[String]) extends Cookie
