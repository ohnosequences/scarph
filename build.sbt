Nice.scalaProject

name := "scarph"

description := "Scala graph API"

organization := "ohnosequences"

bucketSuffix := "era7.com"

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.10.4")

libraryDependencies += {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
              "com.chuusai" %% "shapeless" % "2.0.0"
    case _ => "com.chuusai"  % "shapeless" % "2.0.0" cross CrossVersion.full
  }
}

resolvers += "restlet-releases" at "http://maven.restlet.org/"

libraryDependencies ++= Seq(
  "ohnosequences" %% "cosas" % "0.6.0-SNAPSHOT",
  "com.thinkaurelius.titan" % "titan-all" % "0.5.0",
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "org.scalatest" %% "scalatest" % "2.2.2" % "test"
)
