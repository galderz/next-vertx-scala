name := "mod-lang-scala"

version := "2.0.0-SNAPSHOT"

scalaVersion := "2.11.0"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

libraryDependencies ++= Seq(
  "io.vertx" % "vertx-core" % "2.1RC3",
  "io.vertx" % "vertx-platform" % "2.1RC3",
  "org.scalatest" %% "scalatest" % "2.1.5" % "test"
)
