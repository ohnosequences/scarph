Nice.scalaProject

name          := "scarph"
organization  := "ohnosequences"
description   := "Scala graph API"
scalaVersion  := "2.11.7"

libraryDependencies ++= Seq(
  "ohnosequences"   %% "cosas"       % "0.8.0",
  "org.scalatest"   %% "scalatest"   % "2.2.5" % Test
)

// shows time for each test:
testOptions in Test += Tests.Argument("-oD")

// publish settings
publishArtifact in (Test, packageBin) := true
bucketSuffix := "era7.com"
