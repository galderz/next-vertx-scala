package org.vertx.scala.core.http

case class StatusCode(code: Int, status: String)

object StatusCodes {
  val OK = StatusCode(200, "OK")
}