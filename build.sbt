name := """requests"""

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-deprecation", "-feature")

autoAPIMappings := true

fork in run := true

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.8",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.7" % "test"
)
