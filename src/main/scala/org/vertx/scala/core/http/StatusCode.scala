package org.vertx.scala.core.http

case class StatusCode(code: Int, status: String)

object StatusCodes {
  val OK = StatusCode(200, "OK")
  // TODO: Shouldn't status message for 404 be 'Not Found' ? - https://bugs.eclipse.org/bugs/show_bug.cgi?id=435297
  val NotFound = StatusCode(404, "OK")
}