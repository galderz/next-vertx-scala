package org.vertx.scala

import scala.concurrent.{Future, Promise}

object FutureOps {

  def future[T](func: Promise[T] => Unit): Future[T] = {
    val promise = Promise[T]()
    func(promise)
    promise.future
  }

}
