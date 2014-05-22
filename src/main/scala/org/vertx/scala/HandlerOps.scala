package org.vertx.scala

import org.vertx.java.core.{AsyncResult, Handler}
import scala.language.implicitConversions
import scala.concurrent.{Future, Promise}
import org.vertx.java.core.streams.ReadSupport
import org.vertx.scala.core.http.{HttpServer, HttpServerResponse, HttpClientResponse}
import org.vertx.java.core.http.{ HttpClientResponse => JHttpClientResponse }
import org.vertx.java.core.http.{ HttpServerResponse => JHttpServerResponse }
import org.vertx.java.core.http.{HttpServer => JHttpServer }

object HandlerOps {

  implicit def toVoidHandler(action: => Unit): Handler[Void] =
    new Handler[Void]() {
      override def handle(event: Void) = action
    }

  implicit def promiseVoidHandler(p: => Promise[Unit]): Handler[Void] =
    new Handler[Void]() {
      override def handle(event: Void) = p.success(Unit)
    }

  implicit def toThrowableHandler[T](promise: => Promise[T]): Handler[Throwable] =
    new Handler[Throwable]() {
      override def handle(event: Throwable) = promise.failure(event)
    }

  implicit def toHandlerAR[T](promise: => Promise[T]): Handler[AsyncResult[T]] =
    new Handler[AsyncResult[T]]() {
      override def handle(event: AsyncResult[T]) = {
        if (event.succeeded()) promise.success(event.result())
        else promise.failure(event.cause())
      }
    }

  implicit def promiseVoidHandlerAR[T](p: => Promise[Unit]): Handler[AsyncResult[Void]] =
    toHandlerAR[Void](p.asInstanceOf[Promise[Void]])

  implicit def promiseHttpCRToHandlerWithPause(p: Promise[HttpClientResponse]): Handler[JHttpClientResponse] =
    promiseToHandlerWithPause(HttpClientResponse.apply)(p)

  implicit def promiseHttpServerToHandlerAR(promise: Promise[HttpServer]): Handler[AsyncResult[JHttpServer]] =
    promiseToHandlerAR(HttpServer.apply)(promise)

  def promiseToHandlerAR[S, J](mapFunc: J => S)(promise: Promise[S]): Handler[AsyncResult[J]] =
    new Handler[AsyncResult[J]]() {
      override def handle(event: AsyncResult[J]): Unit = {
        if (event.succeeded()) promise.success(mapFunc(event.result()))
        else promise.failure(event.cause())
      }
    }

  def promiseToHandler[S, J](mapFunc: J => S)(promise: Promise[S]): Handler[J] =
    new Handler[J]() {
      override def handle(event: J): Unit = {
        promise.success(mapFunc(event))
      }
    }

  def promiseToHandlerWithPause[S, J <: ReadSupport[_, _]](mapFunc: J => S)(promise: Promise[S]): Handler[J] =
    new Handler[J]() {
      override def handle(event: J): Unit = {
        event.pause()
        promise.success(mapFunc(event))
      }
    }

  def functionToHandler[S, J](mapFunc: J => S)(func: S => Unit): Handler[J] =
    new Handler[J]() {
      override def handle(event: J): Unit = {
        func(mapFunc(event))
      }
    }


}
