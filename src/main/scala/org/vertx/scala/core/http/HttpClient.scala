package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpClient => JHttpClient }
import org.vertx.java.core.buffer.{ Buffer => JBuffer }
import org.vertx.java.core.http.{ HttpClientResponse => JHttpClientResponse }
import scala.concurrent.{Promise, Future}
import org.vertx.java.core.Handler

final class HttpClient private[scala] (val asJava: JHttpClient) extends AnyVal {

  def port(port: Int): HttpClient = {
    asJava.setPort(port); this
  }

  def port(): Int = asJava.getPort

  def getNow(uri: String): Future[HttpClientResponse] = {
    println("getNow")
    val promise = Promise[HttpClientResponse]()
    // TODO: Refactor handler...
    asJava.exceptionHandler(new Handler[Throwable] {
      override def handle(event: Throwable): Unit = {
        print(event)
        promise.failure(event)
      }
    })
    // TODO: Refactor handler...
    asJava.getNow(uri, new Handler[JHttpClientResponse] {
      override def handle(event: JHttpClientResponse): Unit = {
        println(event)
        event.pause()
        promise.success(HttpClientResponse(event))
      }
    })
    promise.future
  }

  def getNow2[T: HttpClientResponseLike](uri: String): Future[T] = {
    println("getNow2")
    val handlerLike = implicitly[HttpClientResponseLike[T]]
    val promise = Promise[T]()
    // TODO: Refactor handler...
    asJava.exceptionHandler(new Handler[Throwable] {
      override def handle(event: Throwable): Unit = {
        print(event)
        promise.failure(event)
      }
    })
    // TODO: Refactor handler...
    asJava.getNow(uri, new Handler[JHttpClientResponse] {
      override def handle(event: JHttpClientResponse): Unit = {
        println(event)
        //        event.bodyHandler(new Handler[JBuffer] {
        //          override def handle(event: JBuffer): Unit = {
        //            println(event)
        //          }
        //        })

        promise.success(handlerLike.create(event))
      }
    })
    promise.future
  }

}

object HttpClient {
  def apply(asJava: JHttpClient) = new HttpClient(asJava)
}
