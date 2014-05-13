package org.vertx.scala.core

import scala.concurrent.ExecutionContext

object VertxExecutionContext {

  def fromVertx(vertx: => Vertx): ExecutionContext = new VertxExecutionContextImpl(vertx)

  // TODO: Make it a class here?
  private final class VertxExecutionContextImpl(vertx: => Vertx) extends ExecutionContext {
    override def reportFailure(t: Throwable): Unit = {}
      // TODO: Add logger
      // logger.error("Error executing Future in VertxExecutionContext", t)
    override def execute(runnable: Runnable): Unit =
      vertx.runOnContext(runnable.run())
  }

}
