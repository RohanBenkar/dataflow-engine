name := "DataPipeline"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

play.Project.playJavaSettings
