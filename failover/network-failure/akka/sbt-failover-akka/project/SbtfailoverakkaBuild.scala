import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object SbtfailoverakkaBuild extends Build {

  lazy val sbtfailoverakka = Project(
    id = "sbt-failover-akka",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "sbt-failover-akka",
      organization := "net.francesbagual.sandbox.failover",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.1",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2-M3",
      libraryDependencies += "com.typesafe.akka" %% "akka-cluster-experimental" % "2.2-M3"
    ) ++ assemblySettings
  )
}
