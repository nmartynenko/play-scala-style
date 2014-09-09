import play.Play.autoImport._
import PlayKeys._

//project settings

name := "sample-crud-play-scala-style"

version := "1.4"

//dependencies resolvers

resolvers ++= Seq(
    "Sonatype" at "http://search.maven.org/remotecontent?filepath=",
    Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns),
    Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.io/snapshots/"))(Resolver.ivyStylePatterns)
)

//Scala's compiler and runtime settings

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  //show style warnings
  "-Xlint"
)

//dependencies settings

libraryDependencies ++= Seq(
  //test dependencies
  "junit" % "junit" % "4.11" % "test",
  //runtime dependencies
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.hsqldb" % "hsqldb" % "2.3.2",
  //security
  "be.objectify" %% "deadbolt-scala" % "2.3.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  //enable JDBC module for the project
  jdbc,
  cache
)

//apply plugin settings

lazy val scala_style = (project in file(".")).enablePlugins(PlayScala)