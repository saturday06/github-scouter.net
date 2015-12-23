name := "github-scouter-net"

version := "1.1"

lazy val `github-scouter-net` = (project in file(".")).enablePlugins(PlayScala, PhantomJs)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(jdbc, cache, ws,
  "mysql" % "mysql-connector-java" % "5.1.38",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "com.codeborne" % "phantomjsdriver" % "1.2.1" % Test

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

javaOptions in Test += "-Dconfig.file=conf/test.conf"
javaOptions in Test ++= PhantomJs.setup(baseDirectory.value),
