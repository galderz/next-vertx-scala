package org.vertx.scala.platform

import org.vertx.scala.core.Vertx

trait Verticle {

  private var _vertx: Vertx = _

  lazy val vertx: Vertx = _vertx

  def start(): Unit = {}

  private[platform] def setVertx(newVertx: Vertx): Unit = _vertx = newVertx

}
