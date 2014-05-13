package org.vertx.scala.core

import org.vertx.java.core.{ Vertx => JVertx }
import org.vertx.scala.core.http.{HttpClient, HttpServer}
import org.vertx.scala.Handlers._

final class Vertx private[scala] (val asJava: JVertx) extends AnyVal {

  /**
   * Create an HTTP/HTTPS server.
   */
  def createHttpServer(): HttpServer = HttpServer(asJava.createHttpServer())

  /**
   * Create a HTTP/HTTPS client.
   */
  def createHttpClient(): HttpClient = HttpClient(asJava.createHttpClient())

  /**
   * Put the action on the event queue for the current loop (or worker context)
   * so it will be run asynchronously ASAP after this event has been processed.
   */
  def runOnContext(action: => Unit): Unit = asJava.runOnContext(action)

}

object Vertx {
  def apply(asJava: JVertx) = new Vertx(asJava)
}
