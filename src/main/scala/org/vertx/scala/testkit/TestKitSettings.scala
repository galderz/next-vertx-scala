package org.vertx.scala.testkit

object TestKitSettings {

  // TODO: Externalize defaults values

  val TestTimeFactor = System.getProperty("vertx.scala.test.time-factor", "1.0").toDouble
  val DefaultTimeout = System.getProperty("vertx.scala.test.default-timeout", "5").toInt

}
