package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpClient => JHttpClient }
import org.vertx.java.core.http.{ HttpClientRequest => JHttpClientRequest }
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
      asJava.getNow(uri, p)
    }
  }

  def post(uri: String): HttpClientRequest = {
    httpMethod(asJava.post(uri, _))
  }

  def get(uri: String): HttpClientRequest = {
    httpMethod(asJava.get(uri, _))
  }

  def head(uri: String): HttpClientRequest = {
    httpMethod(asJava.head(uri, _))
  }

  def connect(uri: String): HttpClientRequest = {
    httpMethod(asJava.connect(uri, _))
  }

  def put(uri: String): HttpClientRequest = {
    httpMethod(asJava.put(uri, _))
  }

  def delete(uri: String): HttpClientRequest = {
    httpMethod(asJava.delete(uri, _))
  }

  def options(uri: String): HttpClientRequest = {
    httpMethod(asJava.options(uri, _))
  }

  def trace(uri: String): HttpClientRequest = {
    httpMethod(asJava.trace(uri, _))
  }

  def patch(uri: String): HttpClientRequest = {
    httpMethod(asJava.patch(uri, _))
  }

  def request(method: HttpMethod, uri: String): HttpClientRequest = {
    httpMethod(asJava.request(method.name, uri, _))
  }

  private def httpMethod(method: Promise[HttpClientResponse] => JHttpClientRequest): HttpClientRequest = {
    val p = Promise[HttpClientResponse]()
    val clientRequest = method(p)
    HttpClientRequest(clientRequest, p.future)
  }

}

object HttpClient {
  def apply(asJava: JHttpClient) = new HttpClient(asJava)
}
