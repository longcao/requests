# Response content

```tut:invisible
import org.requests._
import org.requests.Implicits._
import org.requests.status._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

You can check the response status code or look up status codes by name:

```tut
val requests = Requests()
val responseF = requests.get(url = "http://httpbin.org/get")
val response: Response = Await.result(responseF, 5.seconds)
response.reason
response.statusCode

requests.codes("Forbidden")

requests.close
```


