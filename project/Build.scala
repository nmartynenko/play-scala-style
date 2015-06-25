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
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype" at "http://search.maven.org/remotecontent?filepath="
  )

  val appDependencies = Seq(
    //test dependencies
    "junit" % "junit" % "4.12" % "test",
    //runtime dependencies
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "com.typesafe.play" %% "play-slick" % "0.8.0",
    "org.hsqldb" % "hsqldb" % "2.3.2",
    //security
    "be.objectify" %% "deadbolt-scala" % "2.3.2",
    "org.mindrot" % "jbcrypt" % "0.3m",
    //enable JDBC module for the project
    jdbc,
    cache
  )

  val defaultScalaVersion = "2.11.4"

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
      scalaSource in Test <<= baseDirectory(_ / "test/scala"),
      javaSource in Test <<= baseDirectory(_ / "test/java"),
      //register tasks
      karmaTask, testAllTask
    ).
    //apply plugin settings
    enablePlugins(PlayScala)


  //Task's definitions

  lazy val karma = taskKey[Unit]("Runs Karma tests")

  lazy val karmaTask = karma := {
    import sbt.Process._
    val statusCode =
    //Windows OS
      (sys.props.get("file.separator").filter(_ == "\\").map(_ => "cmd /c ").getOrElse("") +

        "karma start conf/karma.conf.js").!
    if (statusCode != 0){
      sys.exit(statusCode)
    }
  }

  lazy val testAll = taskKey[Unit]("Runs both Scala specs and Karma tests")

  lazy val testAllTask = testAll <<= karma.dependsOn(test in Test)

}