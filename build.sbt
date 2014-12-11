name := "github-scouter-net"

version := "1.0"

lazy val `github-scouter-net` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(jdbc ,anorm ,cache ,ws)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
