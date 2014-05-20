package org.vertx.scala.core.buffer

import org.vertx.java.core.buffer.{ Buffer => JBuffer }

final class Buffer private[scala] (val asJava: JBuffer) extends AnyVal {

  def length(): Int = asJava.length()

  override def toString: String = asJava.toString

  def toString(enc: String = "UTF-8"): String = asJava.toString(enc)

}

object Buffer {
  def apply(asJava: JBuffer) = new Buffer(asJava)
}
