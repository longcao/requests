# Timeouts

```tut:invisible
import org.requests._
import org.requests.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

You can also set request timeouts, in milliseconds. The returned future will contain a Failure.

```tut
val requests = Requests()
val response = requests.get(
  url = "http://httpbin.org/delay/1",
  timeout = Some(1)).recover {
    case e: java.util.concurrent.TimeoutException => "i failed"
  }

Await.result(response, 5.seconds)

requests.close
```

