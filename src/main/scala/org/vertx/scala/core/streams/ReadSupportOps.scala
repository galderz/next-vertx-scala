package org.vertx.scala.core.streams

import org.vertx.java.core.streams.{ ReadSupport => JReadSupport }

final class ReadSupportOps[J <: JReadSupport[_, _], S] private[scala] (asJava: J, asScala: S) {

  def pause(): S = {
    asJava.pause(); asScala
  }

}
