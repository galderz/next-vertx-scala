package org.vertx.scala.core.http

import org.vertx.java.core.buffer.{ Buffer => JBuffer }
import org.vertx.java.core.http.{ HttpClientResponse => JHttpClientResponse }
import org.vertx.scala.FutureOps._
import org.vertx.scala.HandlerOps._
import org.vertx.scala.core.buffer.Buffer
import scala.concurrent.Future

class HttpClientResponse private[scala] (val asJava: JHttpClientResponse) extends AnyVal {

  def status(): StatusCode = StatusCode(asJava.statusCode(), asJava.statusMessage())

  def body(): Future[Buffer] = {
    future[Buffer]{ p =>
      asJava.bodyHandler(promiseToHandler(Buffer.apply)(p))
      asJava.resume() // resume!!
    }
  }

  def headers(): List[HttpHeader] = {
    asJava.headers()
  }

}

object HttpClientResponse {
  def apply(asJava: JHttpClientResponse) = new HttpClientResponse(asJava)

}
