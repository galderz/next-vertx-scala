package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerResponse => JHttpServerResponse }

final class HttpServerResponse private[scala] (val asJava: JHttpServerResponse) extends AnyVal {

  def end(chunk: String, enc: String = "UTF-8"): Unit = asJava.end(chunk, enc)

}

object HttpServerResponse {
  def apply(asJava: JHttpServerResponse) = new HttpServerResponse(asJava)
}
