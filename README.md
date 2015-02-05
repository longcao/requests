# Requests for Scala

Requests for Scala is a small library for making HTTP requests and built on top of [AsyncHttpClient/async-http-client](https://github.com/AsyncHttpClient/async-http-client) but with more Scala goodies and an API based on the fantastic [Requests: HTTP for Humans](http://docs.python-requests.org/en/latest/) Python library.

## Features

- Default constructor gets you started making HTTP requests quickly
- All request methods return regular `scala.concurrent.Future`s
- Liberal usage of algebraic data types
- Attempts to implement most of the [Requests](http://docs.python-requests.org/en/latest/api/) API where applicable
- Wishlist features:
    - Streaming responses
    - Sessions
    - Redirect history

## Quickstart

```scala
import org.requests._
import org.requests.status.Status
import org.requests.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends App {
  val requests = Requests()
  val response: Future[Response] = requests.get(url = "http://httpbin.org/get")
  val content: Future[String] = response.map(r => new String(r.content))
  val status: Future[Status] = response.map(_.status) // org.requests.status.OK
  
  requests.close // close the underlying AsyncHttpClient when you're done
}
```

## Caveats

This is a personal project of mine for a simpler, boiled down HTTP API without all the cute DSL nuances, and as such there's probably a lot wrong with it. If you need something production-ready (including more robust security settings, SSL, etc.) and generally easy to use, then I recommend you use [Play WS](https://www.playframework.com/documentation/2.3.x/ScalaWS) standalone, from which I took cues implementing a Scala facade over async-http-client.
