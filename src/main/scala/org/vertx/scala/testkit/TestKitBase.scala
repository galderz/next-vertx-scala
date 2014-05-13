package org.vertx.scala.testkit

import org.vertx.java.platform.{PlatformLocator, PlatformManager}
import org.scalatest.{BeforeAndAfterAll, Matchers, Suite, BeforeAndAfter}
import org.vertx.scala.core.{VertxExecutionContext, Vertx}
import org.vertx.scala.platform.Verticle
import java.net.{URLClassLoader, URL}
import scala.collection.mutable
import org.vertx.java.core.{Handler, AsyncResult}
import scala.concurrent.{Future, Await, Promise}
import org.vertx.scala.Handlers._
import scala.concurrent.duration._

trait TestKitBase extends BeforeAndAfterAll with Matchers { this: Suite =>

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
      Await.result(TestKitBase.future, 10 seconds)
    } finally {
      val promise = Promise[Unit]()
      platform.undeployAll(new Handler[AsyncResult[Void]] {
        override def handle(event: AsyncResult[Void]): Unit = {
          promise.success()
        }
      })
      Await.result(promise.future, 10 second)
    }
  }

  def verticleThrows[E <: AnyRef](action: => Future[Any])(implicit manifest: Manifest[E]): Unit = {
    deployVerticle(() => action)
    an [E] should be thrownBy Await.result(TestKitBase.future, 10 seconds)
  }

  private def deployVerticle[T](action: () => Future[T]): Unit = {
    val p = Promise[String]()
    val handler: AsyncResult[String] => Unit = { res =>
      if (res.succeeded()) p.success(res.result()) else p.failure(res.cause())
    }

    TestKitBase.action = action
    platform.deployVerticle(classOf[TestKitBase.TestVerticle].getName, null, findURLs().orNull, 1, null, handler)
    // TODO: Refactor this awaits...
    Await.result(p.future, 60 second) should startWith ("deployment-")
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
      case _ => None
    }
  }

}

object TestKitBase {

  var action: () => Future[Any] = _
  var future: Future[Any] = _

  class TestVerticle extends Verticle {
    override def start(): Unit = {
      future = action.apply()
    }
  }

}
