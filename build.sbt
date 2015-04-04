Nice.scalaProject

name          := "scarph"
description   := "Scala graph API"
organization  := "ohnosequences"
bucketSuffix  := "era7.com"

scalaVersion        := "2.11.6"
crossScalaVersions  := Seq("2.10.5")

libraryDependencies ++= Seq(
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
              "com.chuusai" %% "shapeless" % "2.0.0"
    case _ => "com.chuusai"  % "shapeless" % "2.0.0" cross CrossVersion.full
  },
  "ohnosequences"           %% "cosas"       % "0.6.0",
  //"org.scalaz"              %% "scalaz-core" % "7.1.0",
  "org.scalatest"           %% "scalatest"   % "2.2.4" % Test,
  "org.slf4j"               %  "slf4j-nop"   % "1.7.5" % Test
  // ^ getting rid of the annoying warning about logging ^
)

// shows time for each test:
testOptions in Test += Tests.Argument("-oD")

// no name hashing, funny stuff happens
incOptions := incOptions.value.withNameHashing(false)
