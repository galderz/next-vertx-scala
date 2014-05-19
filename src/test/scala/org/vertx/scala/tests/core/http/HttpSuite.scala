package org.vertx.scala.tests.core.http

import org.scalatest.FunSuite
import org.vertx.scala.testkit.TestKitBase
import org.vertx.scala.core.http._
import org.vertx.scala._
import java.net.UnknownHostException
import org.vertx.scala.core.http.HttpMethods._

class HttpSuite extends FunSuite with TestKitBase {

  val testPort = 8844

  def createHttpServer(): HttpServer = vertx.createHttpServer()

  def createHttpClient(): HttpClient = vertx.createHttpClient()

  // TODO: Add test - An HTTP client should successfully call connect() method
  // TODO: Add test - An HTTP client should successfully send a CONNECT request

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

  test("An HTTP client should retrieve static content using get() method") {
    httpMethod(_.get("/"), "get() me")
  }

  test("An HTTP client should receive response to the put() method") {
    httpMethod(_.put("/"), "put() me")
  }

  test("An HTTP client should receive response to the post() method") {
    httpMethod(_.post("/"), "post() me")
  }

  test("An HTTP client should receive response to the options() method") {
    httpMethod(_.options("/"), "options() me")
  }

  test("An HTTP client should receive response to the trace() method") {
    httpMethod(_.trace("/"), "trace() me")
  }

  test("An HTTP client should receive response to the patch() method") {
    httpMethod(_.patch("/"), "patch() me")
  }

  test("An HTTP client should retrieve static content using GET request") {
    httpMethod(_.request(GET, "/"), "GET me")
  }

  test("An HTTP client should receive response to a PUT request") {
    httpMethod(_.request(PUT, "/"), "PUT me")
  }

  test("An HTTP client should receive response to a POST request") {
    httpMethod(_.request(POST, "/"), "POST me")
  }

  test("An HTTP client should receive response to an OPTIONS request") {
    httpMethod(_.request(OPTIONS, "/"), "OPTIONS me")
  }

  test("An HTTP client should receive response to a TRACE request") {
    httpMethod(_.request(TRACE, "/"), "TRACE me")
  }

  test("An HTTP client should receive response to a PATCH method") {
    httpMethod(_.request(PATCH, "/"), "PATCH me")
  }

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

  test("An HTTP client should successfully send a HEAD request") {
    httpMethod(_.request(HEAD, "/"))
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
