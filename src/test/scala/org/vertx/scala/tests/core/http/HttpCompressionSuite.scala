package org.vertx.scala.tests.core.http

import org.vertx.scala.core.http.{HttpServer, HttpClient}

class HttpCompressionSuite extends HttpSuite {

  override def createHttpServer(): HttpServer =
    super.createHttpServer().compression(compression = true)

  override def createHttpClient(): HttpClient =
    super.createHttpClient().compression(compression = true)

}
