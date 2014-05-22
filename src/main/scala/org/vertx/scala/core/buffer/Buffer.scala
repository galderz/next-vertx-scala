package org.vertx.scala.core.buffer

import org.vertx.java.core.buffer.{ Buffer => JBuffer }

final class Buffer private[scala] (val asJava: JBuffer) extends AnyVal {
  import Buffer._

  def length(): Int = asJava.length()

  override def toString: String = asJava.toString

  def toString(enc: String = "UTF-8"): String = asJava.toString(enc)

  def append[T: BufferType](value: T): Buffer = {
    implicitly[BufferType[T]].append(asJava, value); this
  }

}

object Buffer {
  def apply() = new Buffer(new JBuffer())
  def apply(asJava: JBuffer) = new Buffer(asJava)

  trait BufferType[T] {
    def append(buffer: JBuffer, value: T): JBuffer
  }
  implicit object BufferElem extends BufferType[Buffer] {
    override def append(buffer: JBuffer, value: Buffer) = buffer.appendBuffer(value.asJava)
  }
  implicit object ByteElem extends BufferType[Byte] {
    override def append(buffer: JBuffer, value: Byte) = buffer.appendByte(value)
  }
  implicit object BytesElem extends BufferType[Array[Byte]] {
    override def append(buffer: JBuffer, value: Array[Byte]) = buffer.appendBytes(value)
  }
  implicit object DoubleElem extends BufferType[Double] {
    override def append(buffer: JBuffer, value: Double) = buffer.appendDouble(value)
  }
  implicit object FloatElem extends BufferType[Float] {
    override def append(buffer: JBuffer, value: Float) = buffer.appendFloat(value)
  }
  implicit object IntElem extends BufferType[Int] {
    override def append(buffer: JBuffer, value: Int) = buffer.appendInt(value)
  }
  implicit object LongElem extends BufferType[Long] {
    override def append(buffer: JBuffer, value: Long) = buffer.appendLong(value)
  }
  implicit object ShortElem extends BufferType[Short] {
    override def append(buffer: JBuffer, value: Short) = buffer.appendShort(value)
  }
  implicit object StringElem extends BufferType[String] {
    override def append(buffer: JBuffer, value: String) = buffer.appendString(value)
  }
  implicit object StringWithEncodingElem extends BufferType[(String, String)] {
    override def append(buffer: JBuffer, value: (String, String)) = buffer.appendString(value._1, value._2)
  }


}
