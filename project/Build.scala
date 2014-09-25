import sbt._
import Keys._
import play.Play.autoImport._
import play.PlayScala
import PlayKeys._

object ApplicationBuild extends Build {

  val appName = "sample-crud-play-scala-style"
  val appDescription = "Sample CRUD Play: Scala Style"
  val appVersion = "2.0.0"

  val appResolvers = Seq(
    "Sonatype" at "http://search.maven.org/remotecontent?filepath=",
    Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns),
    Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.io/snapshots/"))(Resolver.ivyStylePatterns)
  )

  val appDependencies = Seq(
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

  val defaultScalaVersion = "2.11.2"

  val appCrossScalaVersions = Seq("2.10.4", defaultScalaVersion)

  val defaultScalacOptions = Seq(
    "-feature",
    "-unchecked",
    "-deprecation",
    //show style warnings
    "-Xlint"
  )

  //specify project
  lazy val scala_style = (project in file(".")).
    //apply project settings
    settings(
      name := appName,
      description := appDescription,
      version := appVersion,
      scalaVersion := defaultScalaVersion,
      crossScalaVersions ++= appCrossScalaVersions,
      scalacOptions ++= defaultScalacOptions,
      resolvers := appResolvers,
      libraryDependencies ++= appDependencies,
      //register tasks
      karmaTask, testAllTask
    ).
    //apply plugin settings
    enablePlugins(PlayScala)


  //Task's definitions

  val karma = taskKey[Unit]("Runs Karma tests")

  val karmaTask = karma := {
    import sbt.Process._
    val statusCode =
    //Windows OS
      if(sys.props.get("file.separator").forall(_ == "\\"))
        "cmd /c karma start conf/karma.conf.js".!
      else
        "karma start conf/karma.conf.js".!
    if (statusCode != 0){
      sys.exit(statusCode)
    }
  }

  val testAll = taskKey[Unit]("Runs both Scala specs and Karma tests")

  val testAllTask = testAll <<= karma.dependsOn(test in Test)

}