package org.requests

import java.io.File
import java.net.URL

import scala.concurrent.Future

trait Cookie
trait PreparedRequest

trait Response {
  def apparentEncoding: String
  def content: Array[Byte]
  def cookies: Seq[Cookie]
  def elasped: Long
  def encoding: String
  def headers: Map[String, ResponseHeader]
  def history: Seq[Response]
  def isPermanentRedirect: Boolean
  def isRedirect: Boolean
  //def iterContent
  //def iterLines
  def json: String
  //def links
  //def raw
  def reason: String
  def request: PreparedRequest
  def statusCode: Int
  def text: String
  def url: String
}

trait Requests {
  def request(
    method: RequestMethod,
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Option[String] = None,
    json: Option[String] = None,
    headers: Seq[RequestHeader] = Seq.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, File] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    //proxies
    //verify
    stream: Boolean = false
    //cert
  ): Future[Response]

  def get(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Option[String] = None,
    json: Option[String] = None,
    headers: Seq[RequestHeader] = Seq.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, File] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    //proxies
    //verify
    stream: Boolean = false
    //cert
  ): Future[Response] = {
    request(RequestMethod.Get, url, params, data, json, headers, cookies, files, timeout, allowRedirects, stream)
  }

  def head: Future[Response]
  def post: Future[Response]
  def put: Future[Response]
  def patch: Future[Response]
  def delete: Future[Response]

  def codes: Map[String, Int]
}

object Requests extends Requests {
  def request(
    method: RequestMethod,
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Option[String] = None,
    json: Option[String] = None,
    headers: Seq[RequestHeader] = Seq.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, File] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    //proxies
    //verify
    stream: Boolean = false
    //cert
  ): Future[Response] = ???

  def head: Future[Response] = ???
  def post: Future[Response] = ???
  def put: Future[Response] = ???
  def patch: Future[Response] = ???
  def delete: Future[Response] = ???

  def codes: Map[String, Int] = ???
}
