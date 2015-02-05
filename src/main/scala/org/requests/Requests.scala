package org.requests

import com.ning.http.client.{
  AsyncHttpClient,
  AsyncHttpClientConfig,
  AsyncHttpClientConfigDefaults,
  AsyncCompletionHandler,
  FluentCaseInsensitiveStringsMap,
  Param => AHCParam,
  ProxyServer,
  Response => AHCResponse,
  RequestBuilder
}

import javax.net.ssl.SSLContext

import org.requests.status.Status

import scala.concurrent.{ Future, Promise }
import scala.collection.JavaConverters.{ mapAsJavaMapConverter, seqAsJavaListConverter }

object Requests {

  /**
   * Constructs a new Requests instance with options available
   * to pass to the underlying AsyncHttpClientConfig.
   */
  def apply(
    verify: Boolean = true,
    sslContext: SSLContext = null
  ): Requests = {
    val config = new AsyncHttpClientConfig.Builder()
      .setUserAgent("Requests.scala/0.1.0")
      .setAcceptAnyCertificate(!verify)
      .setSSLContext(sslContext)

    new Requests(new AsyncHttpClient(config.build))
  }
}

case class Requests(client: AsyncHttpClient) {

  /**
   * Executes an HTTP request.
   *
   * @param method          HTTP verb
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def request(
    method: RequestMethod,
    url: String,
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

    val queryParams: java.util.List[AHCParam] =
      params.to[Seq].map { case (name, value) =>
        new AHCParam(name, value)
      }.asJava

    // configure the request
    val requestBuilder = new RequestBuilder(method.toString)
      .setUrl(url)
      .setFollowRedirects(allowRedirects)
      .setHeaders(nsHeaders)
      .addQueryParams(queryParams)
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
      new AsyncCompletionHandler[AHCResponse]() {
        override def onCompleted(ahcResponse: AHCResponse): AHCResponse = {
          result.success(Response(ahcResponse))
          ahcResponse
        }
        override def onThrowable(t: Throwable): Unit = {
          result.failure(t)
        }
      }
    )

    result.future
  }

  /**
   * Executes a HEAD request.
   *
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def head(
    url: String,
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

  /**
   * Executes a GET request.
   *
   * @param method          HTTP verb
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def get(
    url: String,
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

  /**
   * Executes a POST request.
   *
   * @param method          HTTP verb
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def post(
    url: String,
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

  /**
   * Executes a PUT request.
   *
   * @param method          HTTP verb
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def put(
    url: String,
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

  /**
   * Executes a PATCH request.
   *
   * @param method          HTTP verb
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def patch(
    url: String,
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

  /**
   * Executes a DELETE request.
   *
   * @param method          HTTP verb
   * @param url             request URL
   * @param params          map of query params
   * @param data            HTTP message body
   * @param headers         map of request headers
   * @param cookies         collection of request [[org.requests.Cookie]]s
   * @param auth            optional HTTP authentication configuration
   * @param timeout         request timeout, in milliseconds
   * @param allowRedirects  if true, follow redirects
   * @param proxy           optional proxy configuration
   * @return                a `Future` holding the resulting response
   */
  def delete(
    url: String,
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

  /**
   * A map of HTTP status names to status codes.
   */
  def codes: Map[String, Int] = Status.statusToCodes

  /**
   * Closes the underlying AsyncHttpClient.
   *
   * {{{
   * import org.requests._
   * import org.requests.Implicits._
   *
   * val requests = Requests()
   * val result: Future[Response] = requests.get(url = "http://httpbin.org/get")
   * requests.close
   * }}}
   *
   */
  def close = client.close()
}
