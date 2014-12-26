package org.requests

import com.ning.http.client.{ AsyncHttpClient, AsyncCompletionHandler, Response => NingResponse, RequestBuilder }

import java.io.File
import java.net.URL

import scala.concurrent.{ Future, Promise }

trait PreparedRequest

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
    request(RequestMethod.GET, url, params, data, json, headers, cookies, files, timeout, allowRedirects, stream)
  }

  def head: Future[Response]
  def post: Future[Response]
  def put: Future[Response]
  def patch: Future[Response]
  def delete: Future[Response]

  def codes: Map[String, Int]
}

object Requests extends Requests {
  // figure out client configuration here (timeout, executorservice, etc)
  lazy val asyncHttpClient: AsyncHttpClient = new AsyncHttpClient()

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
  ): Future[Response] = {

    // configure the request
    val requestBuilder = new RequestBuilder(method.toString)
      .setUrl(url.toString)
      .setFollowRedirects(allowRedirects)

    val result = Promise[Response]()

    asyncHttpClient.executeRequest(
      requestBuilder.build(),
      new AsyncCompletionHandler[NingResponse]() {
        override def onCompleted(ningResponse: NingResponse): NingResponse = {
          result.success(Response(ningResponse))
          ningResponse
        }
        override def onThrowable(t: Throwable): Unit = {
          result.failure(t)
        }
      })
    result.future
  }

  def head: Future[Response] = ???
  def post: Future[Response] = ???
  def put: Future[Response] = ???
  def patch: Future[Response] = ???
  def delete: Future[Response] = ???

  def codes: Map[String, Int] = ???
}
