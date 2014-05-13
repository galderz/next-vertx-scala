package org.vertx.scala.platform

import org.vertx.java.platform.{ Verticle => JVerticle }
import org.vertx.java.core.{ Vertx => JVertx }
import org.vertx.scala.core.Vertx

class VerticleAdapter(verticle: Verticle) extends JVerticle {

  override def setVertx(jvertx: JVertx): Unit = {
    verticle.setVertx(Vertx(jvertx))
    super.setVertx(jvertx)
  }

  override def start(): Unit = {
    verticle.start()
  }

}
