package org.vertx.scala.util

import scala.util.Try

object ClassLoaders {

  /**
   * Instantiates the given class name using the classloader provided
   * @param className String containing the class name to instantiate
   * @param classLoader where to find the given class name
   * @tparam T type of instance expected back
   * @return [[scala.util.Success]] containing a new instance of the given
   *        class or [[scala.util.Failure]] with any errors reported
   */
  def newInstance[T](className: String, classLoader: ClassLoader): Try[T] = {
    Try {
      val clazz = classLoader.loadClass(className)
      clazz.newInstance().asInstanceOf[T]
    }
  }

}
