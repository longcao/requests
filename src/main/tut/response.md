# Response content

```tut:invisible
import org.requests._
import org.requests.Implicits._
import org.requests.status.Status
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

The response content is available under response.content as a `Array[Byte]`. Requests for Scala doesn't make any decisions on your behalf when it comes to JSON deserialization/unmarshalling, so a default JSON representation is not available in the response API.

```tut
val requests = Requests()
val responseF = requests.get(url = "http://httpbin.org/get")
val response: Response = Await.result(responseF, 5.seconds)
val contentAsString: String = new String(response.content)

requests.close
```

