package org.vertx.scala.core.streams

import org.vertx.java.core.streams.{ ReadStream => JReadStream }
import scala.concurrent.Future
import org.vertx.scala.FutureOps._
import org.vertx.scala.HandlerOps._

final class ReadStreamOps[J <: JReadStream[_]] private[scala] (val asJava: J) extends AnyVal {

  def end(): Future[Unit] = {
    future[Unit] { p =>
      asJava.endHandler(p)
    }
  }

}
