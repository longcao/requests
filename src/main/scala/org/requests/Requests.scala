package org.requests

import scala.concurrent.Future

trait Header
case class RequestHeader(name: String, value: String) extends Header
case class ResponseHeader(name: String, value: String) extends Header

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
  def request: Future[Response]

  def head: Future[Response]
  def get: Future[Response]
  def post: Future[Response]
  def put: Future[Response]
  def patch: Future[Response]
  def delete: Future[Response]

  def codes: Map[String, Int]
}
