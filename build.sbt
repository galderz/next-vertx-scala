name := "mod-lang-scala"

version := "2.0.0-SNAPSHOT"

scalaVersion := "2.11.0"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers += Resolver.mavenLocal

// Parallel execution must be disabled because the way test verticle gets the
// function to run (and future to wait for it to complete) is via static
// fields, and therefore these can only be modified one verticle at the time
// This is because Vert.x deploy verticle only allows passing in the class name
parallelExecution in Test := false

libraryDependencies ++= Seq(
  "io.vertx" % "vertx-core" % "2.1RC4-SNAPSHOT",
  "io.vertx" % "vertx-platform" % "2.1RC4-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.1.5"
)
