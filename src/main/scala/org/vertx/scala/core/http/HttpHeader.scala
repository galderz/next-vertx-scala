package org.vertx.scala.core.http

import org.vertx.java.core.MultiMap
import scala.annotation.switch
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

abstract class HttpHeader {
  def name: String
  def value: String
}

object HttpHeader {
  import HttpHeaders._

  implicit def toHttpHeaders(headers: MultiMap): List[HttpHeader] = {
    val typedHeaders = ListBuffer[HttpHeader]()
    val it = headers.iterator()
    while (it.hasNext) {
      val header = it.next()
      val httpHeader = header.getKey match {
        case "Content-Length" => `Content-Length`(header.getValue.toLong)
        case "Content-Type" => `Content-Type`(header.getValue)
        case _ => RawHeader(header.getKey, header.getValue)
      }
      typedHeaders += httpHeader
    }
    typedHeaders.toList
  }

}

abstract class HttpHeaderBase extends HttpHeader {
  val name: String = {
    val fqn = getClass.getName
    fqn.substring(fqn.indexOf('$') + 1, fqn.length).replace("$minus", "-")
  }
}

object HttpHeaders {
  case class `Content-Length`(length: Long) extends HttpHeaderBase {
    override val value: String = length.toString
  }

  // TODO: Add charset to Content-Type
  case class `Content-Type`(media: MediaType) extends HttpHeaderBase {
    override val value: String = media.toString
  }

  case class RawHeader(name: String, value: String) extends HttpHeader
}