package org.vertx.scala.testkit

import java.net.{URLClassLoader, URL}
import org.scalatest.{BeforeAndAfterAll, Matchers, Suite}
import org.vertx.java.platform.PlatformLocator
import org.vertx.scala.HandlerOps._
import org.vertx.scala.core.{VertxExecutionContext, Vertx}
import scala.collection.mutable
import scala.concurrent.{Future, Await, Promise}
import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter, File}
import scala.annotation.tailrec
import org.scalatest.matchers.{BePropertyMatchResult, BePropertyMatcher}

trait TestKitBase extends BeforeAndAfterAll with Matchers { this: Suite =>

  import TestKitBase._

  System.setProperty("vertx.langs..", "scala") // override default via system property

  val platform = PlatformLocator.factory.createPlatformManager
  val vertx = Vertx(platform.vertx())
  implicit val executionContext = VertxExecutionContext.fromVertx(vertx)

  override def beforeAll(): Unit = {
    // no-op
  }

  override def afterAll(): Unit = {
    platform.stop()
  }

  def verticle[T](action: => Future[T]): Unit = {
    deployVerticle(() => action)
    try {
      await(TestVerticle.future)
    } finally {
      until[Unit](platform.undeployAll(_))
    }
  }

  def verticleThrows[E <: AnyRef](action: => Future[Any])(implicit manifest: Manifest[E]): Unit = {
    deployVerticle(() => action)
    an [E] should be thrownBy await(TestVerticle.future)
  }

  private def deployVerticle[T](action: () => Future[T]): Unit = {
    TestVerticle.action = action
    until[String] {
      platform.deployVerticle(classOf[TestVerticle].getName, null, findURLs().orNull, 1, null, _)
    } should startWith ("deployment-")
  }

}

object TestKitBase {

  import TestKitSettings._
  import scala.concurrent.duration._

  def until[T](func: Promise[T] => Unit): T = {
    val promise = Promise[T]()
    func(promise)
    await(promise.future)
  }

  def await[T](future: Future[T]): T = {
    Await.result(future, (DefaultTimeout * TestTimeFactor).seconds)
  }

  def generateFile(filename: String, len: Int): (File, String) = {
    val content = generateUnicodeString(len)
    val file = createFile(filename, content)
    (file, content)
  }

  def generateFile(filename: String, content: String): (File, String) = {
    val file = createFile(filename, content)
    (file, content)
  }

  private def createFile(filename: String, content: String): File = {
    val file = new File(System.getProperty("java.io.tmpdir"), filename)
    if (file.exists())
      file.delete()

    val out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))
    try {
      out.write(content)
    } finally {
      out.close()
    }

    file
  }

  def generateUnicodeString(length: Int): String = {
    val builder = new StringBuilder(length)
    for (i <- 0 until length) {
      @tailrec def generateChar(): Char = {
        val c = (0xFFFF * Math.random()).toChar
        // Skip illegal chars
        if ((c >= 0xFFFE && c <= 0xFFFF) || (c >= 0xD800 && c <= 0xDFFF))
          generateChar()
        else
          c
      }

      builder.append(generateChar())
    }
    builder.toString()
  }

  def anInstanceOf[T](implicit manifest: Manifest[T]) = {
    val clazz = manifest.runtimeClass.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) =
        BePropertyMatchResult(clazz.isAssignableFrom(left.getClass),
          "an instance of " + clazz.getName)
    }
  }

  private def findURLs(): Option[Array[URL]] = {
    val urls = mutable.ListBuffer[URL]()
    val pcl = Thread.currentThread.getContextClassLoader
    pcl match {
      case u: URLClassLoader =>
        for (url <- u.getURLs) {
          val stringUrl = url.toString
          if (!stringUrl.endsWith(".jar") && !stringUrl.endsWith(".zip"))
            urls += url
        }
        Some(urls.toArray)
      case _ => Some(Array[URL]())
    }
  }

}
