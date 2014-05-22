package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpClientRequest => JHttpClientRequest }
import scala.concurrent.Future

final class HttpClientRequest private[scala]
    (val asJava: JHttpClientRequest, future: Future[HttpClientResponse]) {

  def end(): Future[HttpClientResponse] = {
    asJava.end()
    future
  }

  def putHeader(header: HttpHeader): HttpClientRequest = {
    asJava.putHeader(header.name, header.value); this
  }

}

object HttpClientRequest {
  def apply(asJava: JHttpClientRequest, future: Future[HttpClientResponse]) =
    new HttpClientRequest(asJava, future)
}
