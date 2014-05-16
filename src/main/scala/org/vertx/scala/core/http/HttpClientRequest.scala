package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpClientRequest => JHttpClientRequest }
import scala.concurrent.Future

final class HttpClientRequest private[scala]
    (val asJava: JHttpClientRequest, future: Future[HttpClientResponse]) {

  def end(): Future[HttpClientResponse] = {
    asJava.end()
    future
  }

}

object HttpClientRequest {
  def apply(asJava: JHttpClientRequest, future: Future[HttpClientResponse]) =
    new HttpClientRequest(asJava, future)
}
