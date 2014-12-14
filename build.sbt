Nice.scalaProject

name          := "scarph"
description   := "Scala graph API"
organization  := "ohnosequences"
bucketSuffix  := "era7.com"


scalaVersion        := "2.11.4"
crossScalaVersions  := Seq("2.10.4", "2.11.2")

libraryDependencies += {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
              "com.chuusai" %% "shapeless" % "2.0.0"
    case _ => "com.chuusai"  % "shapeless" % "2.0.0" cross CrossVersion.full
  }
}

libraryDependencies ++= Seq(
  "ohnosequences"           %% "cosas"            % "0.6.0-SNAPSHOT",
  "org.scalaz"              %% "scalaz-core"      % "7.1.0",
  "com.thinkaurelius.titan" %  "titan-core"       % "0.5.2",
  "com.thinkaurelius.titan" %  "titan-berkeleyje" % "0.5.2"           % "test",
  "org.scalatest"           %% "scalatest"        % "2.2.2"           % "test"
)
