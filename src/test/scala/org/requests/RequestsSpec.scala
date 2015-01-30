package org.requests

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }

trait RequestsSpec extends FlatSpec
  with BeforeAndAfterAll
  with Matchers
  with ScalaFutures
  with TypeCheckedTripleEquals {

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val requests = Requests()

  override def afterAll: Unit = {
    requests.close
  }
}
