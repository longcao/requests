package org.requests

import com.ning.http.client.{
  AsyncHttpClient,
  AsyncCompletionHandler,
  FluentCaseInsensitiveStringsMap,
  Param => NingParam,
  Response => NingResponse,
  RequestBuilder
}

import java.net.URL

import org.requests.status.Status

import scala.concurrent.{ Future, Promise }
import scala.collection.JavaConverters.{ mapAsJavaMapConverter, seqAsJavaListConverter }

object Requests {
  def defaultClient: AsyncHttpClient = new AsyncHttpClient()
}

case class Requests(client: AsyncHttpClient = Requests.defaultClient) {
  def request(
    method: RequestMethod,
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Array[Byte] = Array.empty,
    json: Option[Json] = None,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, BodyPart] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true
    //proxies
    //verify
    //stream: Boolean = false
    //cert
  ): Future[Response] = {

    val nsHeaders: FluentCaseInsensitiveStringsMap = {
      val hs = (headers ++ Cookie.cookiesToHeader(cookies))
        .map { case (name, values) =>
          name -> values.asJava.asInstanceOf[java.util.Collection[String]]
        }.asJava
      new FluentCaseInsensitiveStringsMap(hs)
    }

    val queryParams: java.util.List[NingParam] =
      params.to[Seq].map { case (name, value) =>
        new NingParam(name, value)
      }.asJava

    // configure the request
    val requestBuilder = new RequestBuilder(method.toString)
      .setUrl(url.toString)
      .setFollowRedirects(allowRedirects)
      .setHeaders(nsHeaders)
      .setQueryParams(queryParams)
      .setRequestTimeout(timeout.getOrElse(0)) // default to 0, falls back to client config

    // priority: data > json > files
    val requestBuilderWithBody: RequestBuilder = if (data.nonEmpty) {
      requestBuilder.setBody(data)
    } else if (json.nonEmpty) {
      requestBuilder.setBody(json.get)
    } else if (files.nonEmpty) {
      files.foldLeft(requestBuilder) { case (rb, (name, bodyPart)) =>
        val part = bodyPart.toPart(name)
        rb.addBodyPart(part)
      }
    } else {
      requestBuilder
    }

    val result = Promise[Response]()

    client.executeRequest(
      requestBuilderWithBody.build(),
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

  def get(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Array[Byte] = Array.empty,
    json: Option[Json] = None,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    files: Map[String, BodyPart] = Map.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true
    //proxies
    //verify
    //stream: Boolean = false
    //cert
  ): Future[Response] = {
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
      allowRedirects = allowRedirects)
      //stream = stream)
  }

  def head: Future[Response] = ???
  def post: Future[Response] = ???
  def put: Future[Response] = ???
  def patch: Future[Response] = ???
  def delete: Future[Response] = ???

  def codes: Map[String, Int] = Status.statusToCodes

  def close = client.close()
}
