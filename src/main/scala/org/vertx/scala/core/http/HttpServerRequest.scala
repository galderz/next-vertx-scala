package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerRequest => JHttpServerRequest }

final class HttpServerRequest private[scala] (val asJava: JHttpServerRequest) extends AnyVal {

  def response(): HttpServerResponse = HttpServerResponse(asJava.response)

}

object HttpServerRequest {
  def apply(asJava: JHttpServerRequest) = new HttpServerRequest(asJava)
}
