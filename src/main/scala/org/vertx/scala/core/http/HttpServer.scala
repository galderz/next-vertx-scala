package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServer => JHttpServer }
import scala.concurrent.{Promise, ExecutionContext, Future}
import org.vertx.scala.core.{Vertx, VertxExecutionContext}
import org.vertx.java.core.{AsyncResult, Handler}
import org.vertx.scala.Handlers._

final class HttpServer private[scala] (val asJava: JHttpServer) extends AnyVal {

  // implicit val executionContext = VertxExecutionContext.fromVertx(vertx)

  def handler[T: HandlerLike](f: T => Unit): HttpServer = {
    val handlerLike = implicitly[HandlerLike[T]]
    handlerLike.handle(f, this)
    this
  }

  def listen(port: Int, host: String = "0.0.0.0")(implicit ec: ExecutionContext): Future[HttpServer] = {
    val promise = Promise[HttpServer]()
    // TODO: Refactor handler...
    asJava.listen(port, new Handler[AsyncResult[JHttpServer]] {
      override def handle(event: AsyncResult[JHttpServer]): Unit = {
        if (event.succeeded()) promise.success(HttpServer(event.result()))
        else promise.failure(event.cause())
      }
    })
    promise.future
  }

  def close(): Future[Unit] = {
    val promise = Promise[Unit]()
    asJava.close(new Handler[AsyncResult[Void]] {
      override def handle(event: AsyncResult[Void]): Unit = {
        promise.success()
      }
    })
    promise.future
  }

}

object HttpServer {
  def apply(asJava: JHttpServer) = new HttpServer(asJava)
}
