Nice.scalaProject

name          := "scarph"
description   := "Scala graph API"
organization  := "ohnosequences"
bucketSuffix  := "era7.com"

scalaVersion        := "2.11.7"
crossScalaVersions  := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  "ohnosequences"   %% "cosas"       % "0.7.1-SNAPSHOT",
  "org.scalatest"   %% "scalatest"   % "2.2.5" % Test
)

// shows time for each test:
testOptions     in Test               += Tests.Argument("-oD")
publishArtifact in (Test, packageBin) := true

// incOptions := incOptions.value.withNameHashing(false)

// dependencyOverrides ++= Set(
//   "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3",
//   "org.scala-lang.modules" %% "scala-xml" % "1.0.3"
// )
