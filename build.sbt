Nice.scalaProject

name          := "scarph"
description   := "Scala graph API"
organization  := "ohnosequences"
bucketSuffix  := "era7.com"

scalaVersion        := "2.11.6"
crossScalaVersions  := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  "ohnosequences"   %% "cosas"       % "0.6.0",
  "ohnosequences"   %% "statika"     % "2.0.0-feature-no-typesets-SNAPSHOT",
  "org.scalatest"   %% "scalatest"   % "2.2.4" % Test
)

// shows time for each test:
testOptions     in Test               += Tests.Argument("-oD")
publishArtifact in (Test, packageBin) := true

incOptions := incOptions.value.withNameHashing(false)