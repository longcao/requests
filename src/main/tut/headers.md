# Request headers

```tut:invisible
import org.requests._
import org.requests.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

Adding in headers is simple and is exposed as another method parameter:

```tut
val requests = Requests()
val headers = Map("my-header" -> Seq("my-value"))
val response = requests.get(url = "http://httpbin.org/get", headers = headers)
  
requests.close
```


