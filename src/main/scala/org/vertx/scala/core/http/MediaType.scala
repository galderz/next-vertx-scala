package org.vertx.scala.core.http

import scala.annotation.switch
import scala.language.implicitConversions

case class MediaType(main: String, subtype: String) {
  override def toString: String = main + '/' + subtype
}

object MediaType {
  import MediaTypes._

  implicit def toMediaType(mediaType: String): MediaType = {
    (mediaType: @switch) match {
      case "text/html" => `text/html`
      case "text/plain" => `text/plain`
    }
  }
}

object MediaTypes {

  val `text/html` = MediaType("text", "html")
  val `text/plain` = MediaType("text", "plain")

}
