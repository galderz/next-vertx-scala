package org.vertx.scala.tests.core.http

import java.io.File
import java.net.{URLEncoder, UnknownHostException}
import org.scalatest.FunSuite
import org.vertx.scala._
import org.vertx.scala.core.buffer.Buffer
import org.vertx.scala.core.http.HttpHeaders._
import org.vertx.scala.core.http.HttpMethods._
import org.vertx.scala.core.http.MediaTypes._
import org.vertx.scala.core.http._
import org.vertx.scala.testkit.TestKitBase
import org.vertx.scala.testkit.TestKitBase._
import scala.concurrent.Promise

class HttpSuite extends FunSuite with TestKitBase {

  val testPort = 8844

  val compression: Compression = Uncompressed

  private def createHttpServer(): HttpServer =
    vertx.createHttpServer().compression(compression)

  private def createHttpClient(): HttpClient =
    vertx.createHttpClient().compression(compression)

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
        body.toString shouldBe msg
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

  test("An HTTP server can respond sending a file") {
    val (file, content) = generateFile("test-send-file.html", 10000)
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](_.response().sendFile(file))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("some-uri")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        resp.headers() should contain(`Content-Type`(MediaTypes.`text/html`))
        body.toString shouldBe content
        assertContentLength(resp, file.length())
      }
    }
  }

  private def assertContentLength(resp: HttpClientResponse, length: Long): Unit = {
    if (compression.enabled())
      resp.headers() should not contain anInstanceOf[`Content-Length`]
    else
      resp.headers() should contain(`Content-Length`(length))
  }

  test("If an HTTP server can't find the file to send, it should return 404") {
    verticle {
      val doesNotExistFile = new File("doesnotexist.html")
      val server = createHttpServer().handler[HttpServerRequest](_.response().sendFile(doesNotExistFile))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("some-uri")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.NotFound
        resp.headers() should contain(`Content-Type`(MediaTypes.`text/html`))
        body.toString shouldBe "<html><body>Resource not found</body><html>"
      }
    }
  }

  test("If an HTTP server can't find the file to send, it can respond with a 404 page") {
    verticle {
      val (fallbackFile, content) = generateFile("my-404-page.html", "<html><body>This is my 404 page</body></html>")
      val server = createHttpServer().handler[HttpServerRequest](
        _.response().sendFile(new File("doesnotexist.html"), Some(fallbackFile))
      )
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("some-uri")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.NotFound
        resp.headers() should contain(`Content-Type`(MediaTypes.`text/html`))
        body.toString shouldBe content
      }
    }
  }

  test("An HTTP server can respond sending a file and overriding HTTP headers") {
    val (file, content) = generateFile("test-send-file.html", 10000)
    verticle {
      val server = createHttpServer().handler[HttpServerRequest](
        _.response().putHeader(`Content-Type`(`text/plain`)).sendFile(file))
      for {
        listening <- server.listen(testPort)
        resp <- createHttpClient().port(testPort).getNow("some-uri")
        body <- resp.body()
      } yield {
        resp.status() shouldBe StatusCodes.OK
        resp.headers() should contain(`Content-Type`(`text/plain`))
        body.toString shouldBe content
        assertContentLength(resp, file.length())
      }
    }
  }

  test("An HTTP client should be able to upload form attributes via URL with encoding") {
    verticle {
      val form = Promise[MultiMap]()
      val server = createMultiPartHttpServer(form)
      for {
        listening <- server.listen(testPort)
        encoded = URLEncoder.encode("vert x", "UTF-8")
        data = Buffer().append(s"framework=$encoded&runson=jvm", "UTF-8")
        resp <- createHttpClient().port(testPort).post("/form")
          .putHeader(`Content-Length`(data.length()))
          .putHeader(`Content-Type`(`application/x-www-form-urlencoded`))
          .write(data).end()
        body <- resp.body()
        attributes <- form.future
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.length() shouldBe 0
        attributes.size shouldBe 2
        attributes("framework").head shouldBe "vert x"
        attributes("runson").head shouldBe "jvm"
      }
    }
  }

  test("An HTTP client should be able to upload repeated form attributes via URL") {
    verticle {
      val form = Promise[MultiMap]()
      val server = createMultiPartHttpServer(form)
      for {
        listening <- server.listen(testPort)
        data = Buffer().append("origin=unittest&origin=scalatest&login=admin%40foo.bar&pass+word=admin")
        resp <- createHttpClient().port(testPort).post("/form")
          .putHeader(`Content-Length`(data.length()))
          .putHeader(`Content-Type`(`application/x-www-form-urlencoded`))
          .write(data).end()
        body <- resp.body()
        attributes <- form.future
      } yield {
        resp.status() shouldBe StatusCodes.OK
        body.length() shouldBe 0
        attributes.size shouldBe 3
        attributes("origin") should contain("unittest")
        attributes("origin") should contain("scalatest")
        attributes("login").head shouldBe "admin@foo.bar"
        attributes("pass word").head shouldBe "admin"
      }
    }
  }

  private def createMultiPartHttpServer(form: Promise[MultiMap]): HttpServer = {
    createHttpServer().handler[HttpServerRequest] { req =>
      if (req.uri().startsWith("/form")) {
        req.response().chunked(chunked = true)
        req.expectMultiPart(expect = true)
        req.end().map(_ => form.success(req.formAttributes()))
        req.response().end()
      }
    }
  }

}
