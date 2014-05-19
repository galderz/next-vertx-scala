package org.vertx.scala.core.http

sealed trait HttpMethod { def name: String }

object HttpMethods {
  case object GET extends HttpMethod { val name = "GET" }
  case object PUT extends HttpMethod { val name = "PUT" }
  case object POST extends HttpMethod { val name = "POST" }
  case object OPTIONS extends HttpMethod { val name = "OPTIONS" }
  case object TRACE extends HttpMethod { val name = "TRACE" }
  case object PATCH extends HttpMethod { val name = "PATCH" }
  case object HEAD extends HttpMethod { val name = "HEAD" }
}
