import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaAppPackaging)

organization := "com.codurance"
name := "gr8craft"
version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.slf4s" %% "slf4s-api" % "1.7.12",
  "ch.qos.logback" % "logback-classic" % "1.1.2",

  "junit" % "junit" % "4.12" % Test,
  "org.scalatest" %% "scalatest" % "2.2.4" % Test,
  "info.cukes" %% "cucumber-scala" % "1.2.4" % Test,
  "info.cukes" % "cucumber-junit" % "1.2.4" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % Test
)
