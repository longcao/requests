name := """requests"""

version := "0.0.1"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-deprecation", "-feature")

fork in run := true

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.9.3"
)
