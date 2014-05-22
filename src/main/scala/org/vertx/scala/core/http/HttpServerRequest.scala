package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerRequest => JHttpServerRequest }
import org.vertx.scala.MultiMap

final class HttpServerRequest private[scala] (val asJava: JHttpServerRequest) extends AnyVal {

  // TODO: Consider creating a typed URI! (could form parameters be part of it?)

  def response(): HttpServerResponse = HttpServerResponse(asJava.response)

  def uri(): String = asJava.uri()

  def expectMultiPart(expect: Boolean): HttpServerRequest = {
    asJava.expectMultiPart(expect); this
  }

  def formAttributes(): MultiMap = asJava.formAttributes()

}

object HttpServerRequest {
  def apply(asJava: JHttpServerRequest) = new HttpServerRequest(asJava)
}
