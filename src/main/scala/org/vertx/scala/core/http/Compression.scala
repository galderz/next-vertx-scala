package org.vertx.scala.core.http

sealed trait Compression {
  def enabled(): Boolean
}

case object Compressed extends Compression {
  override val enabled: Boolean = true
}

case object Uncompressed extends Compression {
  override val enabled: Boolean = false
}
