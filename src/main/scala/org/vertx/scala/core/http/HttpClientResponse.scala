package org.vertx.scala.core.http

import org.vertx.java.core.http.{HttpClientResponse => JHttpClientResponse}
import org.vertx.java.core.buffer.{ Buffer => JBuffer }
import scala.concurrent.{Promise, Future}
import org.vertx.scala.core.buffer.Buffer
import org.vertx.java.core.Handler

class HttpClientResponse private[scala] (val asJava: JHttpClientResponse) extends AnyVal {

  def status(): StatusCode = StatusCode(asJava.statusCode(), asJava.statusMessage())

  def body(): Future[Buffer] = {
    //println("body")
    val promise = Promise[Buffer]()
    // TODO: Refactor handler...
    asJava.bodyHandler(new Handler[JBuffer] {
      override def handle(event: JBuffer): Unit = {
        print(event)
        promise.success(Buffer(event))
      }
    })
    asJava.resume() // resume!!
    promise.future
  }

}

class HttpClientBodyResponse private[scala] (val asJava: JHttpClientResponse, bodyFuture: Future[Buffer]) {
  def status(): StatusCode = StatusCode(asJava.statusCode(), asJava.statusMessage())
  def body(): Future[Buffer] = bodyFuture
}

object HttpClientResponse {
  def apply(asJava: JHttpClientResponse) = new HttpClientResponse(asJava)

}

trait HttpClientResponseLike[T] {
  def create(asJava: JHttpClientResponse): T
}

object HttpClientResponseLike {
  implicit object HttpClientBodyResponseLike extends HttpClientResponseLike[HttpClientBodyResponse] {
    override def create(asJava: JHttpClientResponse): HttpClientBodyResponse = {
      val promise = Promise[Buffer]()
      asJava.bodyHandler(new Handler[JBuffer] {
        override def handle(event: JBuffer): Unit = {
          println(event)
          promise.success(Buffer(event))
        }
      })
      new HttpClientBodyResponse(asJava, promise.future)
    }
  }
}

