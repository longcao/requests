package org.requests

import com.ning.http.client.{
  AsyncHttpClient,
  AsyncHttpClientConfig,
  AsyncHttpClientConfigDefaults,
  AsyncCompletionHandler,
  ProxyServer,
  Response => AHCResponse
}

import javax.net.ssl.SSLContext

import org.requests.status.Status

import scala.concurrent.{ Future, Promise }

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
      .setUserAgent("Requests.scala/0.1.6")
      .setAcceptAnyCertificate(!verify)
      .setSSLContext(sslContext)

    new Requests(new AsyncHttpClient(config.build))
  }
}

class Requests(client: AsyncHttpClient) {

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
    val startTimeMillis = System.currentTimeMillis

    val request = Request(
      method = method,
      url = url,
      params = params,
      data = data,
      headers = headers,
      cookies = cookies,
      auth = auth,
      timeout = timeout,
      allowRedirects = allowRedirects,
      proxy = proxy)

    val result = Promise[Response]()

    client.executeRequest(
      request.prepare,
      new AsyncCompletionHandler[AHCResponse]() {
        override def onCompleted(ahcResponse: AHCResponse): AHCResponse = {
          result.success(
            Response.construct(
              ahcr = ahcResponse,
              elasped = System.currentTimeMillis - startTimeMillis,
              request = request))

          ahcResponse
        }
        override def onThrowable(t: Throwable): Unit = {
          result.failure(t)
          ()
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
   * Executes an OPTIONS request.
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
  def options(
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
      method = RequestMethod.OPTIONS,
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
  def close() = client.close()
}
