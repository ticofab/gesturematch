name := "backend"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

play.Project.playScalaSettings
