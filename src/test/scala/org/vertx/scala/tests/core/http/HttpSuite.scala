package org.vertx.scala.tests.core.http

import org.scalatest.FunSuite
import org.vertx.scala.testkit.TestKitBase
import org.vertx.scala.core.http._
import org.vertx.scala._
import java.net.UnknownHostException

class HttpSuite extends FunSuite with TestKitBase {

  val testPort = 8844

  def createHttpServer(): HttpServer = vertx.createHttpServer()

  def createHttpClient(): HttpClient = vertx.createHttpClient()

  // TODO: Add test - An HTTP client should successfully call connect() method

  test("An HTTP server should serve static content") {
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](_.response().end("hello world!"))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("/")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.string() shouldBe "hello world!"
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
        body.string() shouldBe "hello encrypted world!"
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

  test("An HTTP client should retrieve static content using post() method") {
    httpMethod(_.post("/"), "post() me")
  }

  test("An HTTP client should retrieve static content using get() method") {
    httpMethod(_.get("/"), "get() me")
  }

  test("An HTTP client should retrieve static content using put() method") {
    httpMethod(_.put("/"), "put() me")
  }

  // TODO: Add tests for delete, options, trace...

  private def httpMethod(method: HttpClient => HttpClientRequest, msg: String): Unit = {
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](_.response().end(msg))
      for {
        listening <- server.listen(testPort)
        resp <- method(createHttpClient().port(testPort)).end()
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.string() shouldBe msg
      }
    }
  }

  test("An HTTP client should successfully call head() method") {
    httpMethod(_.head("/"))
  }

  private def httpMethod(method: HttpClient => HttpClientRequest): Unit = {
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](_.response().end("ignore"))
      for {
        listening <- server.listen(testPort)
        resp <- method(createHttpClient().port(testPort)).end()
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.length() shouldBe 0
      }
    }
  }

}
