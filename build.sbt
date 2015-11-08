organization := "org.requests"

name := "requests"

version := "0.1.6"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, scalaMajor)) if scalaMajor >= 11 =>
    Seq(
      "-Ywarn-unused",
      "-Ywarn-unused-import"
    )
  case _ => Seq.empty
})
scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)}
scalacOptions in (Test, console) <<= (scalacOptions in (Compile, console))

initialCommands in console :=
  """
    import org.requests._
    import org.requests.status.Status
    import org.requests.Implicits._
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.Future
  """

autoAPIMappings := true

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

tutSettings

bintrayPackageLabels := Seq("scala", "requests", "http", "http client", "async")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.31",
  "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.10" % "test"
)
