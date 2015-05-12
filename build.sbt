name := """play-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
       CoffeeScriptKeys.bare := false
    )

scalaVersion := "2.11.1"

scalacOptions += "-target:jvm-1.7"

resolvers += "sorm Scala 2.11 fork" at "http://markusjura.github.io/sorm"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.sorm-framework" % "sorm" % "0.4.1",
  "com.h2database" % "h2" % "1.4.177",
  "io.prediction" % "client" % "0.8.3"
)

dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value

fork in run := true
