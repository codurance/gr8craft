import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

enablePlugins(JavaAppPackaging)

organization := "com.codurance"
name := "gr8craft"
version := "0.1"

scalaVersion := "2.11.7"

resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

val akkaVersion = "2.3.11"
val twitter4jVersion = "4.0.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "1.0.7",
  "org.twitter4j" % "twitter4j-core" % twitter4jVersion,
  "org.twitter4j" % "twitter4j-async" % twitter4jVersion,
  "org.slf4s" %% "slf4s-api" % "1.7.12",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc42" % Runtime,

  "org.scalatest" %% "scalatest" % "2.2.4" % Test,
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "info.cukes" %% "cucumber-scala" % "1.2.4" % Test,
  "info.cukes" % "cucumber-junit" % "1.2.4" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.0.5" % Test
)
