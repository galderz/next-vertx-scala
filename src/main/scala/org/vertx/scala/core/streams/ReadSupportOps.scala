package org.vertx.scala.core.streams

import org.vertx.java.core.streams.{ ReadSupport => JReadSupport }

final class ReadSupportOps[J <: JReadSupport[_, _], S] private[scala] (val asJava: J, asScala: S) {
  // TODO: Ideally these, and similar classes, should be AnyVals to avoid wrapper per wrapped instance...
  // A workaround can be found in https://gist.github.com/galderz/bbd07b608ace868d4794
  // where a single wrapper is used per type instead of per instance

  def pause(): S = {
    asJava.pause(); asScala
  }

}
