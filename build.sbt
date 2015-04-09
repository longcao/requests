organization := "org.requests"

name := "requests"

version := "0.1.3"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.11.6")

scalacOptions ++= Seq(
  "-Xlint",
  "-deprecation",
  "-feature",
  "-unchecked"
)

autoAPIMappings := true

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

tutSettings

bintrayPublishSettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("scala", "requests", "http", "http client", "async")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.8",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.7" % "test"
)
