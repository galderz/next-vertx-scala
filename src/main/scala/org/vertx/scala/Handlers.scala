package org.vertx.scala

import org.vertx.java.core.{AsyncResult, Handler}
import scala.language.implicitConversions

object Handlers {

  implicit def toHandlerVoid(action: => Unit) =
    new Handler[Void]() {
      override def handle(event: Void) = action
    }

  implicit def toHandlerAsyncResult[T](func: AsyncResult[T] => Unit) =
    new Handler[AsyncResult[T]]() {
      override def handle(event: AsyncResult[T]) = func(event)
    }

  implicit def scalaToJavaHandlerAsyncResult[S, J](mapFunc: J => S)(func: AsyncResult[S] => Unit) =
    new Handler[AsyncResult[J]]() {
      override def handle(event: AsyncResult[J]) = {
        val scalaEvent = new AsyncResult[S]() {
          override def result(): S = mapFunc(event.result())
          override def cause(): Throwable = event.cause()
          override def succeeded(): Boolean = event.succeeded()
          override def failed(): Boolean = event.failed()
        }
        func(scalaEvent)
      }
    }

}
