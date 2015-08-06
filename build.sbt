Nice.scalaProject

name          := "scarph"
description   := "Scala graph API"
organization  := "ohnosequences"
bucketSuffix  := "era7.com"

scalaVersion        := "2.11.6"
crossScalaVersions  := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  "ohnosequences"   %% "cosas"       % "0.6.0",
  "org.scalatest"   %% "scalatest"   % "2.2.4" % Test
)

// shows time for each test:
testOptions     in Test               += Tests.Argument("-oD")
publishArtifact in (Test, packageBin) := true

incOptions := incOptions.value.withNameHashing(false)

dependencyOverrides ++= Set(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3"
)

wartremoverErrors in (Compile, compile) := Seq()
