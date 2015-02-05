package org.requests

import com.ning.http.client.cookie.{ Cookie => AHCCookie }

object Cookie {

  /**
   * Constructs a Cookie from the java AHCCookie.
   */
  def apply(ahcc: AHCCookie): Cookie = {
    Cookie(
      domain = ahcc.getDomain,
      expires = if (ahcc.getExpires == -1) None else Some(ahcc.getExpires),
      httpOnly = ahcc.isHttpOnly,
      maxAge = if (ahcc.getMaxAge == -1) None else Some(ahcc.getMaxAge),
      name = if (ahcc.getName.isEmpty) None else Some(ahcc.getName),
      path = ahcc.getPath,
      secure = ahcc.isSecure,
      value = if (ahcc.getValue.isEmpty) None else Some(ahcc.getValue))
  }

  /**
   * Converts a Seq[Cookie] into a flattened cookie header in the form of:
   * "Cookie" -> "name1=value2; name2=value2;" etc
   *
   * This is used mainly because the conversion from Cookie -> AHCCookie
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

case class Cookie(
  domain: String,
  expires: Option[Long],
  httpOnly: Boolean,
  maxAge: Option[Int],
  name: Option[String],
  path: String,
  secure: Boolean,
  value: Option[String])
