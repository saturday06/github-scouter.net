name := "github-scouter-net"

version := "1.0"

lazy val `github-scouter-net` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(jdbc, cache, ws,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "com.typesafe.play" %% "play-slick" % "0.8.1")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
