package org.requests

import com.ning.http.client.{
  AsyncHttpClient,
  AsyncCompletionHandler,
  FluentCaseInsensitiveStringsMap,
  Response => NingResponse,
  RequestBuilder
}

import java.io.File
import java.net.URL

import scala.concurrent.{ Future, Promise }
import scala.collection.JavaConverters.{ mapAsJavaMapConverter, seqAsJavaListConverter }

trait Requests {
  protected def defaultClient: AsyncHttpClient = new AsyncHttpClient()

  def request(
    method: RequestMethod,
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Option[String] = None,
    json: Option[String] = None,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, File] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    //proxies
    //verify
    stream: Boolean = false
    //cert
  )(implicit client: AsyncHttpClient = defaultClient): Future[Response]

  def get(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Option[String] = None,
    json: Option[String] = None,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, File] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    //proxies
    //verify
    stream: Boolean = false
    //cert
  )(implicit client: AsyncHttpClient = defaultClient): Future[Response] = {
    request(
      method = RequestMethod.GET,
      url = url,
      params = params,
      data = data,
      json = json,
      headers = headers,
      cookies = cookies,
      files = files,
      timeout = timeout,
      allowRedirects = allowRedirects,
      stream = stream)
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
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, File] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    //proxies
    //verify
    stream: Boolean = false
    //cert
  )(implicit client: AsyncHttpClient = defaultClient): Future[Response] = {

    val nsHeaders: FluentCaseInsensitiveStringsMap = {
      val hs = headers.map { case (name, values) =>
          name -> values.asJava.asInstanceOf[java.util.Collection[String]]
      }.asJava
      new FluentCaseInsensitiveStringsMap(hs)
    }

    // configure the request
    val requestBuilder = new RequestBuilder(method.toString)
      .setUrl(url.toString)
      .setFollowRedirects(allowRedirects)
      .setHeaders(nsHeaders)

    val result = Promise[Response]()

    client.executeRequest(
      requestBuilder.build(),
      new AsyncCompletionHandler[NingResponse]() {
        override def onCompleted(ningResponse: NingResponse): NingResponse = {
          result.success(Response(ningResponse))
          ningResponse
        }
        override def onThrowable(t: Throwable): Unit = {
          result.failure(t)
        }
      }
    )

    result.future
  }

  def head: Future[Response] = ???
  def post: Future[Response] = ???
  def put: Future[Response] = ???
  def patch: Future[Response] = ???
  def delete: Future[Response] = ???

  def codes: Map[String, Int] = ???
}
