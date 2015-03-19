# Quickstart

Some imports to start out...

```tut:silent
import org.requests._
import org.requests.Implicits._
import org.requests.status.Status
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

And now let's make a simple GET request. For the sake of simplicity, we'll block on the returned `Future[Response]` but generally you shouldn't do this in your application logic.

```tut
val requests = Requests()
val response: Future[Response] = requests.get(url = "http://httpbin.org/get")
println(Await.result(response, 5.seconds))
  
requests.close // close the underlying AsyncHttpClient when you're done
```
