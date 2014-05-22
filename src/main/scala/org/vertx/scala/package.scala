package org.vertx

import language.implicitConversions
import org.vertx.java.core.http.{ HttpClient => JHttpClient }
import org.vertx.java.core.http.{ HttpClientRequest => JHttpClientRequest }
import org.vertx.java.core.http.{ HttpClientResponse => JHttpClientResponse }
import org.vertx.java.core.http.{ HttpServer => JHttpServer }
import org.vertx.java.core.http.{ HttpServerRequest => JHttpServerRequest }
import org.vertx.scala.core.SSLSupportOps
import org.vertx.scala.core.http._
import org.vertx.scala.core.streams.{WriteStreamOps, ReadStreamOps, ReadSupportOps}

package object scala {

  type MultiMap = collection.mutable.MultiMap[String, String]

  @inline implicit def httpCRToReadSupport(target: HttpClientResponse) =
    new ReadSupportOps[JHttpClientResponse, HttpClientResponse](target.asJava, target)
  @inline implicit def httpServerToSSLSupport(target: HttpServer) =
    new SSLSupportOps[JHttpServer, HttpServer](target.asJava, target)
  @inline implicit def httpClientToSSLSupport(target: HttpClient) =
    new SSLSupportOps[JHttpClient, HttpClient](target.asJava, target)
  @inline implicit def httpSRToReadStream(target: HttpServerRequest) =
    new ReadStreamOps[JHttpServerRequest](target.asJava)
  @inline implicit def httpCRToWriteStream(target: HttpClientRequest) =
    new WriteStreamOps[JHttpClientRequest, HttpClientRequest](target.asJava, target)


}
