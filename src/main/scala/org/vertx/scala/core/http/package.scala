package org.vertx.scala.core

import org.vertx.java.core.{ MultiMap => JMultiMap }
import org.vertx.scala.MultiMap
import scala.collection.mutable
import scala.language.implicitConversions

package object http {

  implicit def multiMapToScalaMultiMap(n: JMultiMap): mutable.MultiMap[String, String] = {
    new JMultiMapWrapper(n)
  }

  private class JMultiMapWrapper(val underlying: JMultiMap) extends mutable.MultiMap[String, String] {

    override def addBinding(key: String, value: String): this.type = {
      underlying.add(key, value)
      this
    }

    override def removeBinding(key: String, value: String): this.type = {
      val it = underlying.iterator()
      while (it.hasNext) {
        val next = it.next()
        if (next.getKey.equalsIgnoreCase(key) && next.getValue == value)
          it.remove()
      }
      this
    }

    override def entryExists(key: String, p: (String) => Boolean): Boolean = {
      val it = underlying.iterator()
      while (it.hasNext) {
        val next = it.next()
        if (next.getKey.equalsIgnoreCase(key) && p(next.getValue))
          return true
      }
      false
    }

    override def iterator: Iterator[(String, mutable.Set[String])] = {
      val mm = new mutable.HashMap[String, mutable.Set[String]] with MultiMap
      val it = underlying.iterator()
      while (it.hasNext) {
        val next = it.next()
        mm.addBinding(next.getKey, next.getValue)
      }
      mm.iterator
    }

    override def get(key: String): Option[mutable.Set[String]] = {
      val set = mutable.HashSet[String]()
      val it = underlying.iterator()
      while (it.hasNext) {
        val next = it.next()
        if (next.getKey.equalsIgnoreCase(key))
          set.add(next.getValue)
      }
      if (seq.isEmpty) None else Some(set)
    }

    override def -=(key: String): this.type = {
      underlying.remove(key)
      this
    }

    override def +=(kv: (String, mutable.Set[String])): this.type = {
      kv._2.foreach { v =>
        underlying.add(kv._1, v)
      }
      this
    }

  }

}
