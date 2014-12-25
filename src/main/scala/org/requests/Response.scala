package org.requests

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

