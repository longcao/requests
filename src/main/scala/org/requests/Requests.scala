package org.requests

import com.ning.http.client.{
  AsyncHttpClient,
  AsyncHttpClientConfig,
  AsyncHttpClientConfigDefaults,
  AsyncCompletionHandler,
  FluentCaseInsensitiveStringsMap,
  Param => NingParam,
  ProxyServer,
  Response => NingResponse,
  RequestBuilder
}

import java.net.URL

import org.requests.status.Status

import scala.concurrent.{ Future, Promise }
import scala.collection.JavaConverters.{ mapAsJavaMapConverter, seqAsJavaListConverter }

object Requests {
  def apply(
    verify: Boolean = AsyncHttpClientConfigDefaults.defaultAcceptAnyCertificate
  ): Requests = {
    val config = new AsyncHttpClientConfig.Builder()
      .setAcceptAnyCertificate(verify)

    new Requests(new AsyncHttpClient(config.build))
  }
}

case class Requests(client: AsyncHttpClient) {
  def request(
    method: RequestMethod,
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
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
      .setProxyServer(proxy.getOrElse(null))

    val requestBuilderWithBody: RequestBuilder = data match {
      case EmptyData => requestBuilder
      case ByteArrayData(ba) => requestBuilder.setBody(ba)
      case StringData(s) => requestBuilder.setBody(s)
      case FormData(formValues) =>
        formValues.foldLeft(requestBuilder) { case (rb, (name, value)) =>
          rb.addFormParam(name, value)
        }
      case MultipartData(bodyParts) =>
        bodyParts.foldLeft(requestBuilder) { case (rb, bp) =>
          rb.addBodyPart(bp.toPart)
        }
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
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    //auth
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
    //cert
  ): Future[Response] = {
    request(
      method = RequestMethod.GET,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def head: Future[Response] = ???
  def post: Future[Response] = ???
  def put: Future[Response] = ???
  def patch: Future[Response] = ???
  def delete: Future[Response] = ???

  def codes: Map[String, Int] = Status.statusToCodes

  def close = client.close()
}
