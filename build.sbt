name := """play-product"""
organization := "com.gaon"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.1"

libraryDependencies += guice

libraryDependencies ++= Seq(
  javaJdbc,
  javaWs,
  javaJpa,
  javaForms,
  "com.amazonaws" % "aws-java-sdk" % "1.11.46",
  "mysql" % "mysql-connector-java" % "8.0.17",
  "com.typesafe.play" %% "play-json" % "2.8.1"
)

libraryDependencies ++= Seq(
  "org.hibernate" % "hibernate-core" % "5.4.0.Final",
  "org.projectlombok" % "lombok" % "1.16.16",
  "io.jsonwebtoken" % "jjwt" % "0.9.1"
)

PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
