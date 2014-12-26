package org.requests

/**
 * Algebraic data types (ADTs) representing various HTTP methods.
 * Note: they must be capitalized so that calling .toString on them
 * returns capitalized HTTP methods, which are case-sensitive.
 */
sealed trait RequestMethod
object RequestMethod {
  case object HEAD   extends RequestMethod
  case object GET    extends RequestMethod
  case object POST   extends RequestMethod
  case object PUT    extends RequestMethod
  case object PATCH  extends RequestMethod
  case object DELETE extends RequestMethod
}

