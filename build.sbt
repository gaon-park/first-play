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

// https://mvnrepository.com/artifact/org.hibernate/hibernate-core
libraryDependencies ++= Seq(
  "org.hibernate" % "hibernate-core" % "5.4.0.Final",
  "org.projectlombok" % "lombok" % "1.16.16"
)

PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
