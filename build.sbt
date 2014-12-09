name := "github-scouter-net"

version := "1.0"

lazy val `github-scouter-net` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(jdbc ,anorm ,cache ,ws)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "polymer" % "0.5.1",
  "org.webjars" % "polymer-platform" % "0.4.1"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
