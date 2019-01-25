name := "ScannerIO"

version := "0.2"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-zio" % "0.5.3" ,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.scalamock" %% "scalamock" % "4.1.0" % Test
)