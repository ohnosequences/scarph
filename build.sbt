Nice.scalaProject

name := "scarph"

description := "Scala graph API"

organization := "ohnosequences"

bucketSuffix := "era7.com"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4")

libraryDependencies += {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
              "com.chuusai" %% "shapeless" % "2.0.0"
    case _ => "com.chuusai"  % "shapeless" % "2.0.0" cross CrossVersion.full
  }
}

libraryDependencies ++= Seq(
  "com.thinkaurelius.titan" % "titan-all" % "0.4.4",
  "ohnosequences" %% "pointless" % "0.6.0-SNAPSHOT",
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test"
)

dependencyOverrides ++= Set(
  "tomcat" % "jasper-compiler" % "5.5.23",
  "tomcat" % "jasper-runtime"  % "5.5.23"
)
