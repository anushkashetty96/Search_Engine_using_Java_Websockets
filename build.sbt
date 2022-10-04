name := """gitterific"""

version := "2.8.x"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.6"

Test / testOptions := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

libraryDependencies += guice

libraryDependencies ++= Seq(
  "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5",
//  "org.mockito" % "mockito-core" % "2.10.0" % "test",
  "org.json" % "json" % "20210307",
  "org.mockito" % "mockito-core" % "2.22.0" % "test",

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.14",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.14",
  caffeine
)

// disabled until https://github.com/playframework/playframework/issues/9845 is solved
//scalacOptions ++= List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation")
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
)

Test / jacocoExcludes := Seq(
  "controllers.Reverse*",
  "controllers.javascript.*",
  "jooq.*",
  "Module",
  "router.Routes*",
  "*.routes*",
  "*views.html*",
)

