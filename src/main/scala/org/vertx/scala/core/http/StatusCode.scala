package org.vertx.scala.core.http

import scala.annotation.switch

case class StatusCode(code: Int, status: String)

//object StatusCode {
//  import StatusCodes._
//  implicit def int2StatusCode(code: Int): StatusCode = {
//    (code: @switch) match {
//      case 200 => OK
//    }
//  }
//}

object StatusCodes {
  val OK = StatusCode(200, "OK")
}