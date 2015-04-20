Nice.scalaProject

name          := "scarph"
description   := "Scala graph API"
organization  := "ohnosequences"
bucketSuffix  := "era7.com"

scalaVersion        := "2.11.6"
crossScalaVersions  := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  "ohnosequences"           %% "cosas"       % "0.6.0",
  "org.scalatest"           %% "scalatest"   % "2.2.4" % Test,
  "org.slf4j"               %  "slf4j-nop"   % "1.7.5" % Test
  // ^ getting rid of the annoying warning about logging ^
)

// shows time for each test:
testOptions in Test += Tests.Argument("-oD")

publishArtifact in (Test, packageBin) := true

// no name hashing, funny stuff happens
incOptions := incOptions.value.withNameHashing(false)
