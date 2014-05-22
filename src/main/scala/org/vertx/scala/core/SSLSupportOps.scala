package org.vertx.scala.core

import org.vertx.java.core.{ SSLSupport => JSSLSupport }

final class SSLSupportOps[J <: JSSLSupport[_], S] private[scala] (asJava: J, asScala: S) {

  def ssl(ssl: Boolean): S = {
    asJava.setSSL(ssl); asScala
  }

  def ssl(): Boolean = asJava.isSSL

  def keyStorePath(path: String): S = {
    asJava.setKeyStorePath(path); asScala
  }

  def keyStorePath(): String = asJava.getKeyStorePath

  def keyStorePassword(pwd: String): S = {
    asJava.setKeyStorePassword(pwd); asScala
  }

  def keyStorePassword(): String = asJava.getKeyStorePassword

  def trustStorePath(path: String): S = {
    asJava.setTrustStorePath(path); asScala
  }

  def trustStorePath(): String = asJava.getTrustStorePath

  def trustStorePassword(pwd: String): S = {
    asJava.setTrustStorePassword(pwd); asScala
  }

  def trustStorePassword(): String = asJava.getTrustStorePassword

}
