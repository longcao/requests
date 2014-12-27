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
}

case class CookieImpl(
  domain: String,
  expires: Option[Long],
  maxAge: Option[Int],
  name: Option[String],
  path: String,
  secure: Boolean,
  value: Option[String]) extends Cookie
