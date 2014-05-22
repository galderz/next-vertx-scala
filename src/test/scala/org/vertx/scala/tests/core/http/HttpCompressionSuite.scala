package org.vertx.scala.tests.core.http

import org.vertx.scala.core.http._

class HttpCompressionSuite extends HttpSuite {

  override val compression: Compression = Compressed

}
