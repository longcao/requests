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
import javax.net.ssl.SSLContext

import org.requests.status.Status

import scala.concurrent.{ Future, Promise }
import scala.collection.JavaConverters.{ mapAsJavaMapConverter, seqAsJavaListConverter }

object Requests {
  def apply(
    verify: Boolean = true,
    sslContext: SSLContext = null
  ): Requests = {
    val config = new AsyncHttpClientConfig.Builder()
      .setAcceptAnyCertificate(!verify)
      .setSSLContext(sslContext)

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
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
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
      .setRealm(auth.map(_.toRealm).getOrElse(null))

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

  def head(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
  ): Future[Response] = {
    request(
      method = RequestMethod.HEAD,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def get(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
  ): Future[Response] = {
    request(
      method = RequestMethod.GET,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def post(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
  ): Future[Response] = {
    request(
      method = RequestMethod.POST,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def put(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
  ): Future[Response] = {
    request(
      method = RequestMethod.PUT,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def patch(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
  ): Future[Response] = {
    request(
      method = RequestMethod.PATCH,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def delete(
    url: URL,
    params: Map[String, String] = Map.empty,
    data: Data = EmptyData,
    headers: Map[String, Seq[String]] = Map.empty,
    cookies: Seq[Cookie] = Seq.empty,
    auth: Option[Auth] = None,
    timeout: Option[Int] = None,
    allowRedirects: Boolean = true,
    proxy: Option[ProxyServer] = None
  ): Future[Response] = {
    request(
      method = RequestMethod.DELETE,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)
  }

  def codes: Map[String, Int] = Status.statusToCodes
  def close = client.close()
}
