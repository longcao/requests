# Passing parameters

```tut:invisible
import org.requests._
import org.requests.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

Query string construction in the URL is done for you by passing in `params`, a simple Map[String, String]:

```tut
val requests = Requests()
val params = Map("k1" -> "v1")
val response = requests.get(url = "http://httpbin.org/get", params = params)
println(Await.result(response.map(_.url), 5.seconds))
  
requests.close
```

