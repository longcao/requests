# POST requests

```tut:invisible
import org.requests._
import org.requests.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
```

You can send POST request data in different forms: as bytes, strings, form-encoded, and multipart.

```tut
val requests = Requests()

val bytes = "some bytes".getBytes
val response = requests.post(url = "http://httpbin.org/post", data = bytes)

val string = "a string"
val response = requests.post(url = "http://httpbin.org/post", data = string)

val map = Map("key1" -> "value1")
val response = requests.post(url = "http://httpbin.org/post", data = map)

val stringBodyPart = StringBodyPart("name", "value")
val fileBodyPart = FileBodyPart("file1", new java.io.File("src/test/resources/filebody.txt"))
val byteArrayBodyPart = ByteArrayBodyPart("baName", "bytesValue".getBytes)
val multipart = List(stringBodyPart, fileBodyPart, byteArrayBodyPart)
val response = requests.post(url = "http://httpbin.org/post", data = multipart)
  
requests.close
```


