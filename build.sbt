lazy val commonSettings: Seq[Setting[_]] =
  Nice.scalaProject ++
  Seq[Setting[_]](
    organization := "ohnosequences",
    bucketSuffix := "era7.com",
    scalaVersion := "2.11.0",
    crossScalaVersions := Seq("2.10.4", "2.11.0"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.1.6" % "test"
    ),
    dependencyOverrides ++= Set(
      "tomcat" % "jasper-compiler" % "5.5.23",
      "tomcat" % "jasper-runtime"  % "5.5.23"
    )
  )

// subprojects:
lazy val core = Project("scarph-core", file("scarph-core")) settings(commonSettings: _*)
lazy val titan = Project("scarph-titan", file("scarph-titan")) settings(commonSettings: _*) dependsOn core

// root project is only for aggregating:
lazy val root = Project("scarph-all", file(".")) settings(commonSettings: _*) aggregate(core, titan) settings(
  name := "scarph-all",
  publish := {},
  GithubRelease.assets := Seq(),
  Literator.docsMap := Map()
)
