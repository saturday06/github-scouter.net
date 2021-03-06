name := "github-scouter-net"

version := "1.0"

lazy val `github-scouter-net` = (project in file(".")).enablePlugins(PlayScala, SbtPhantomJs)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(jdbc, cache, ws,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0" % "test")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

javaOptions in Test += "-Dconfig.file=conf/test.conf"

(test in Test) <<= (test in Test) dependsOn(installPhantomJs)
