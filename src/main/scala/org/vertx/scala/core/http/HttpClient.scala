package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpClient => JHttpClient }
import org.vertx.scala.FutureOps._
import org.vertx.scala.HandlerOps._
import scala.concurrent.{Promise, Future}

final class HttpClient private[scala] (val asJava: JHttpClient) extends AnyVal {

  def port(port: Int): HttpClient = {
    asJava.setPort(port); this
  }

  def port(): Int = asJava.getPort

  def compression(compression: Boolean): HttpClient = {
    asJava.setTryUseCompression(compression); this
  }

  def compression(): Boolean = asJava.getTryUseCompression

  def getNow(uri: String): Future[HttpClientResponse] = {
    future[HttpClientResponse] { p =>
      asJava.exceptionHandler(p)
      asJava.getNow(uri, promiseToHandlerWithPause(HttpClientResponse.apply)(p))
    }
  }

  def post(uri: String): HttpClientRequest = {
    val promise = Promise[HttpClientResponse]()
    val clientRequest = asJava.post(uri, promiseToHandlerWithPause(HttpClientResponse.apply)(promise))
    HttpClientRequest(clientRequest, promise.future)
  }

  def get(uri: String): HttpClientRequest = {
    val promise = Promise[HttpClientResponse]()
    val clientRequest = asJava.get(uri, promiseToHandlerWithPause(HttpClientResponse.apply)(promise))
    HttpClientRequest(clientRequest, promise.future)
  }

}

object HttpClient {
  def apply(asJava: JHttpClient) = new HttpClient(asJava)
}
