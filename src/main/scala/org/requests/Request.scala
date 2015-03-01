package org.requests

import com.ning.http.client.{
  FluentCaseInsensitiveStringsMap,
  Param => AHCParam,
  ProxyServer,
  Request => AHCRequest,
  RequestBuilder
}

import scala.collection.JavaConverters.{ mapAsJavaMapConverter, seqAsJavaListConverter }

case class Request(
  method: RequestMethod,
  url: String,
  params: Map[String, String],
  data: Data,
  headers: Map[String, Seq[String]],
  cookies: Seq[Cookie],
  auth: Option[Auth],
  timeout: Option[Int],
  allowRedirects: Boolean,
  proxy: Option[ProxyServer]) {

  lazy val ahcHeaders: FluentCaseInsensitiveStringsMap = {
    val hs = (headers ++ Cookie.cookiesToHeader(cookies))
      .map { case (name, values) =>
        name -> values.asJava.asInstanceOf[java.util.Collection[String]]
      }.asJava
    new FluentCaseInsensitiveStringsMap(hs)
  }

  lazy val queryParams: java.util.List[AHCParam] = {
    params.to[Seq].map { case (name, value) =>
      new AHCParam(name, value)
    }.asJava
  }

  /**
   * Prepares the request and builds the underlying AHC Request object.
   */
  def prepare: AHCRequest = {
    val requestBuilder = new RequestBuilder(method.toString)
      .setUrl(url)
      .setFollowRedirects(allowRedirects)
      .setHeaders(ahcHeaders)
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

    requestBuilderWithBody.build()
  }
}
