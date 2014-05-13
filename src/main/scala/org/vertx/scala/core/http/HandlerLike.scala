package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerRequest => JHttpServerRequest }
import org.vertx.java.core.Handler

trait HandlerLike[T] {
  def handle(fn: T => Unit, s: HttpServer)
}

object HandlerLike {
  implicit object HttpServerRequestHandler extends HandlerLike[HttpServerRequest] {
    def handle(fn: HttpServerRequest => Unit, s: HttpServer) = {
      s.asJava.requestHandler(new Handler[JHttpServerRequest] {
        override def handle(event: JHttpServerRequest): Unit = {
          fn(HttpServerRequest(event))
        }
      })
    }
  }
}
