package org.requests

trait Header
case class RequestHeader(name: String, value: String) extends Header
case class ResponseHeader(name: String, value: String) extends Header

