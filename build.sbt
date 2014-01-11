name := "backend"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

play.Project.playScalaSettings
