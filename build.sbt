
name := "ArcPercolation"

version := "1.0"

scalaVersion := "2.11.5"

scalaSource in Compile := baseDirectory.value / "src"
javaSource in Compile := baseDirectory.value / "src"

//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"
libraryDependencies += "org.scala-lang" % "scala-swing" % "2.11+"
libraryDependencies += "org.scala-lang" % "scala-actors" % "2.11+"

fork in run := true
baseDirectory in run := file(".")

val outJarName = settingKey[String]("output jar name")
outJarName := s"arc-percolation-${version.value}.jar"

mainClass in assembly := Some("program.GenImages")
test in assembly := {}
assemblyJarName in assembly := outJarName.value