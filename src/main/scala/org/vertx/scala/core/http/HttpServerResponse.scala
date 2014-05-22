package org.vertx.scala.core.http

import org.vertx.java.core.http.{ HttpServerResponse => JHttpServerResponse }
import org.vertx.scala.FutureOps._
import org.vertx.scala.HandlerOps._
import scala.concurrent.Future
import java.io.File

final class HttpServerResponse private[scala] (val asJava: JHttpServerResponse) extends AnyVal {

  def end(): Unit = asJava.end()

  def end(chunk: String, enc: String = "UTF-8"): Unit = asJava.end(chunk, enc)

  def sendFile(filename: File, notFoundResource: Option[File] = None): Future[Unit] = {
    future[Unit] { p =>
      asJava.sendFile(filename.getAbsolutePath, notFoundResource.map(_.getAbsolutePath).orNull, p)
    }
  }

  def putHeader(header: HttpHeader): HttpServerResponse = {
    asJava.putHeader(header.name, header.value); this
  }

  def chunked(chunked: Boolean): HttpServerResponse = {
    asJava.setChunked(chunked); this
  }

}

object HttpServerResponse {
  def apply(asJava: JHttpServerResponse) = new HttpServerResponse(asJava)
}
