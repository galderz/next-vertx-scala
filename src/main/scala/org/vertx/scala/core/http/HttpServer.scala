package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerRequest => JHttpServerRequest }
import org.vertx.java.core.http.{HttpServer => JHttpServer }
import org.vertx.scala.FutureOps._
import org.vertx.scala.HandlerOps._
import scala.concurrent.{ExecutionContext, Future}

final class HttpServer private[scala] (val asJava: JHttpServer) extends AnyVal {

  import HttpServer._

  def handler[T: HttpServerHandlerLike](f: T => Unit): HttpServer = {
    val handlerLike = implicitly[HttpServerHandlerLike[T]]
    handlerLike.handle(f, this); this
  }

  def compression(compression: Boolean): HttpServer = {
    asJava.setCompressionSupported(compression); this
  }

  def compression(): Boolean = asJava.isCompressionSupported

  def listen(port: Int, host: String = "0.0.0.0")(implicit ec: ExecutionContext): Future[HttpServer] = {
    future[HttpServer](p => asJava.listen(port, host, promiseToHandlerAR(HttpServer.apply)(p)))
  }

  def close(): Future[Unit] = {
    future[Unit](p => asJava.close(p))
  }

}

object HttpServer {
  def apply(asJava: JHttpServer) = new HttpServer(asJava)

  trait HttpServerHandlerLike[T] {
    def handle(fn: T => Unit, s: HttpServer)
  }

  object HttpServerHandlerLike {
    implicit object HttpServerRequestHandler extends HttpServerHandlerLike[HttpServerRequest] {
      def handle(func: HttpServerRequest => Unit, s: HttpServer) = {
        s.asJava.requestHandler(functionToHandler(HttpServerRequest.apply)(func))
      }
    }
  }

}
