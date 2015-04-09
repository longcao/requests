# Response cookies

```tut:invisible
import org.requests._
import org.requests.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

Response cookies are also available:

```tut
val requests = Requests()
val responseF = requests.get(url = "http://httpbin.org/cookies")
val response: Response = Await.result(responseF, 5.seconds)
response.cookies

requests.close
```

