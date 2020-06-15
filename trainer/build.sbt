name := "akka-http"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.20"
val akkaHttpVersion = "10.1.7"
val scalaTestVersion = "3.0.5"

resolvers += Resolver.mavenLocal

enablePlugins(JavaAppPackaging)
//unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

libraryDependencies ++= Seq(
  //streaming module
  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
//  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  // testing
//  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
//  "org.scalatest" %% "scalatest" % scalaTestVersion,


)

libraryDependencies += "com.dyptan" % "streaming" % "1.1-SNAPSHOT"
