package org.vertx

import org.vertx.java.core.http.{ HttpClientResponse => JHttpClientResponse }
import org.vertx.java.core.http.{ HttpServer => JHttpServer }
import org.vertx.java.core.http.{ HttpClient => JHttpClient }
import org.vertx.scala.core.http.{HttpClient, HttpServer, HttpClientResponse}
import org.vertx.scala.core.streams.ReadSupportOps
import org.vertx.scala.core.SSLSupportOps
import language.implicitConversions

package object scala {

  @inline implicit def httpCRToReadSupport(target: HttpClientResponse) =
    new ReadSupportOps[JHttpClientResponse, HttpClientResponse](target.asJava, target)
  @inline implicit def httpServerToSSLSupport(target: HttpServer) =
    new SSLSupportOps[JHttpServer, HttpServer](target.asJava, target)
  @inline implicit def httpClientToSSLSupport(target: HttpClient) =
    new SSLSupportOps[JHttpClient, HttpClient](target.asJava, target)

}
