name          := "scarph"
organization  := "ohnosequences"
description   := "Scala graph API"

libraryDependencies ++= Seq(
  "ohnosequences" %% "cosas"     % "0.8.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

// shows time for each test:
testOptions in Test += Tests.Argument("-oD")

// publish settings
publishArtifact in (Test, packageBin) := true
bucketSuffix := "era7.com"

wartremoverErrors in (Compile, compile) := Seq()
wartremoverErrors in (Test, compile) := Seq()
// wartremoverErrors := Warts.allBut(Wart.Any)
