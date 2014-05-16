package org.vertx.scala.testkit

import org.vertx.scala.platform.Verticle
import scala.concurrent.Future

class TestVerticle extends Verticle {
  override def start(): Unit = {
    TestVerticle.future = TestVerticle.action.apply()
  }
}

object TestVerticle {
  var action: () => Future[Any] = _
  var future: Future[Any] = _
}