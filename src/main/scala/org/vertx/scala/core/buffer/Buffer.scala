package org.vertx.scala.core.buffer

import org.vertx.java.core.buffer.{ Buffer => JBuffer }

final class Buffer private[scala] (val asJava: JBuffer) extends AnyVal {

  override def toString: String = asJava.toString

}

object Buffer {
  def apply(asJava: JBuffer) = new Buffer(asJava)
}
