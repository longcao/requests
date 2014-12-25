package org.requests

sealed trait RequestMethod
object RequestMethod {
  case object Head   extends RequestMethod
  case object Get    extends RequestMethod
  case object Post   extends RequestMethod
  case object Put    extends RequestMethod
  case object Patch  extends RequestMethod
  case object Delete extends RequestMethod
}

