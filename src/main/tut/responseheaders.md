# Response headers

```tut:invisible
import org.requests._
import org.requests.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

Response headers are available under a hashmap. These may have multiple values if the server responds with multiple values per key.

```tut
val requests = Requests()
val responseF = requests.get(url = "http://httpbin.org/get")
val response: Response = Await.result(responseF, 5.seconds)
response.headers

requests.close
```


