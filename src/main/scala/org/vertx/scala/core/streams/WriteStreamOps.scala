package org.vertx.scala.core.streams

import org.vertx.java.core.streams.{ WriteStream => JWriteStream }
import org.vertx.scala.core.buffer.Buffer

class WriteStreamOps[J <: JWriteStream[_], S] private[scala] (val asJava: J, asScala: S) {

  def write(data: Buffer): S = {
    asJava.write(data.asJava); asScala
  }

}
