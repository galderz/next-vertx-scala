package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerRequest => JHttpServerRequest }
import org.vertx.scala.core.http.HttpServerResponse

final class HttpServerRequest private[scala] (val asJava: JHttpServerRequest) extends AnyVal {

  def response(): HttpServerResponse = HttpServerResponse(asJava.response)

}

object HttpServerRequest {
  def apply(asJava: JHttpServerRequest) = new HttpServerRequest(asJava)
}
