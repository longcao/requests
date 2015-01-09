name := """requests"""

version := "0.0.1"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-deprecation", "-feature")

fork in run := true

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.3",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)
