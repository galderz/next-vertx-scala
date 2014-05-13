package org.vertx.scala.platform.impl

import org.vertx.java.platform.{ Container => JContainer }
import org.vertx.java.platform.{ Verticle => JVerticle }
import org.vertx.java.platform.{ VerticleFactory => JVerticleFactory }
import org.vertx.java.core.logging.Logger
import org.vertx.java.core.{ Vertx => JVertx }
import org.vertx.scala.core.Vertx
import org.vertx.scala.platform.{VerticleAdapter, Verticle}
import org.vertx.scala.util.ClassLoaders
import scala.util.{Failure, Success}

class ScalaVerticleFactory extends JVerticleFactory {
  private var vertx: Vertx = _
  private var cl: ClassLoader = _

  override def init(jvertx: JVertx, jcontainer: JContainer, cl: ClassLoader): Unit = {
    this.vertx = Vertx(jvertx)
    this.cl = cl
  }

  override def close(): Unit = {}

  override def reportException(logger: Logger, t: Throwable): Unit =
    logger.error("Scala verticle threw exception", t)

  override def createVerticle(main: String): JVerticle = {
    ClassLoaders.newInstance[Verticle](main, cl) match {
      case Success(verticle) => new VerticleAdapter(verticle)
      case Failure(t) => throw t
    }
  }

}
