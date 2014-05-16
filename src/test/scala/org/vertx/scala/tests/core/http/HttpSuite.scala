package org.vertx.scala.tests.core.http

import org.scalatest.FunSuite
import org.vertx.scala.testkit.TestKitBase
import org.vertx.scala.core.http.{HttpClient, HttpServer, StatusCodes, HttpServerRequest}
import org.vertx.scala._
import java.net.UnknownHostException

class HttpSuite extends FunSuite with TestKitBase {

  val testPort = 8844

  def createHttpServer(): HttpServer = vertx.createHttpServer()

  def createHttpClient(): HttpClient = vertx.createHttpClient()

  test("An HTTP server should respond successfully") {
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](_.response().end("ignore"))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("/")
      } yield {
        resp.status() shouldBe StatusCodes.OK
      }
    }
  }

  test("An HTTP server should serve static content") {
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](_.response().end("hello world!"))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("/")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.toString shouldBe "hello world!"
      }
    }
  }

  test("An HTTPS server should serve static content") {
    verticle {
      val server = createHttpServer()
        .ssl(ssl = true)
        .keyStorePath("./src/test/keystores/server-keystore.jks").keyStorePassword("wibble")
        .trustStorePath("./src/test/keystores/server-truststore.jks").trustStorePassword("wibble")

      server.handler[HttpServerRequest](_.response().end("hello encrypted world!"))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort)
          .ssl(ssl = true)
          .keyStorePath("./src/test/keystores/client-keystore.jks").keyStorePassword("wibble")
          .trustStorePath("./src/test/keystores/client-truststore.jks").trustStorePassword("wibble")
          .getNow("/")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.toString shouldBe "hello encrypted world!"
      }
    }
  }

  test("An HTTP server should fail to start with an invalid port") {
    verticleThrows[IllegalArgumentException] {
      val server = createHttpServer().handler[HttpServerRequest]{ r => } // Handler must be set :|
      server.listen(1128371831)
    }
  }

  test("An HTTP server should fail to start with an invalid host") {
    verticleThrows[UnknownHostException] {
      val server = createHttpServer().handler[HttpServerRequest]{ r => } // Handler must be set :|
      server.listen(testPort, "iqwjdoqiwjdoiqwdiojwd")
    }
  }

}
